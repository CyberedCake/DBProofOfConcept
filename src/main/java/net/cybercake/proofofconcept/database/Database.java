package net.cybercake.proofofconcept.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import net.cybercake.proofofconcept.Main;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Database {

    public static Datastore datastore;
    private MongoClient client;

    // initializes the database and establishes a connection
    public Database(Main instance) {
        ServerAddress address = new ServerAddress(instance.getStrConfEntry("hostname"), instance.getConfigEntry(Integer.class, "port")); // hostname, port
        MongoCredential credential = MongoCredential.createCredential(instance.getStrConfEntry("username"), instance.getStrConfEntry("database"), instance.getStrConfEntry("password").toCharArray());
        ConnectionString connectionString = new ConnectionString("mongodb://" + credential.getUserName() + ":" + URLEncoder.encode(String.valueOf(Objects.requireNonNull(credential.getPassword(), "Password cannot be null")), StandardCharsets.UTF_8) + "@" + address.getHost() + ":" + address.getPort());

        client = MongoClients.create(
                MongoClientSettings.builder()
                        .applicationName(Main.getInstance().getPluginMeta().getDisplayName())
                        .credential(credential)
                        .applyToSocketSettings(builder -> {
                            builder.connectTimeout(15, TimeUnit.SECONDS);
                            builder.readTimeout(15, TimeUnit.SECONDS);
                        })
                        .applyToClusterSettings(builder -> {
                            builder.serverSelectionTimeout(15, TimeUnit.SECONDS);
                        })
                        .applyToConnectionPoolSettings(builder -> {
                            builder.maxWaitTime(15, TimeUnit.SECONDS);
                        })
                        .applyConnectionString(connectionString)
                        .build()
        );
        datastore = Morphia.createDatastore(client, credential.getSource());
        datastore.getMapper().map(User.class);
        datastore.ensureIndexes();
    }

    public static User create(UUID uuid) { // create the user by creating an instance of User then saving it
        User user = new User(Bukkit.getOfflinePlayer(uuid));
        save(user);
        return user;
    }
    public static Query<User> query(UUID uuid) { return datastore.find(User.class).filter(Filters.eq("uuid", uuid.toString())); }
    public static void delete(UUID uuid) { query(uuid).delete(); } // remove the user from the database
    public interface Executable { User run(User user); } // class to handle executable -- used for anon lambdas (quality-of-life feature)
    public static void save(User user) { datastore.save(user); } // saves the user to the datastore (effectively committing it to the database)
    public static @Nullable User find(UUID uuid) { return query(uuid).first(); } // returns a user via a provided uuid, provided it exists
    public static void execute(UUID user, Executable executable) { save(executable.run(find(user) == null ? create(user) : find(user))); } // find a user, allow the user to modify it in executable, and re-commit it to the database (auto save)

}
