package net.cybercake.proofofconcept;

import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.proofofconcept.database.Database;

public final class Main extends CyberAPI {

    public static Database database;

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        startCyberAPI(
                Settings.builder()
                        .name("proof of concept")
                        .mainPackage("net.cybercake.proofofconcept")
                        .build()
        );

        saveDefaultConfig();
        reloadConfig();

        database = new Database(this);

        Log.info("Loaded " + this.getPluginMeta().getDisplayName() + " in " + (System.currentTimeMillis()-mss) + "ms");
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        Log.info("Loaded " + this.getPluginMeta().getDisplayName() + " in " + (System.currentTimeMillis()-mss) + "ms");
    }

    public <T> T getConfigEntry(Class<T> objectType, String path) {
        try {
            T t = this.getMainConfig().values().getObject(path, objectType);
            if (t == null) throw new NullPointerException();
            return t; // should NEVER be null based on condition above
        } catch (
                Exception exception) { // throwing illegalargumentexception so it doesn't interfere with other actions (really just checking if object is null)
            throw new IllegalArgumentException("Failed to get that config entry from path '" + path + "' with type '" + objectType.getCanonicalName() + "'", exception);
        }
    }
    public String getStrConfEntry(String path) { return getConfigEntry(String.class, path); }

}
