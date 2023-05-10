package net.cybercake.proofofconcept.database;

import dev.morphia.annotations.*;
import dev.morphia.utils.IndexType;
import org.bukkit.OfflinePlayer;

import java.util.Date;

@Entity(value = "user")
@Indexes(value = @Index(fields = @Field(value = "uuid", type = IndexType.TEXT)))
public class User {

    public User() { } // do not use - required for morphia
    public User(OfflinePlayer player) { // when the object is created for the first time
        this.uuid = player.getUniqueId().toString();
        this.kills = 0L; // using a long because why not
    }

    @Id @Indexed public String uuid;
    public long kills;
    public long playtime;

}
