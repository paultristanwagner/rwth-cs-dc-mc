package de.rwth_aachen.cs_dc_mc;

import de.rwth_aachen.cs_dc_mc.database.SQLDatabase;
import de.rwth_aachen.cs_dc_mc.database.SQLiteDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class Plugin extends JavaPlugin {

    private SQLDatabase database;

    @Override
    public void onEnable() {
        getLogger().info( "Plugin enabled." );

        // Create the data folder
        File dataFolder = getDataFolder();
        if ( !dataFolder.exists() ) {
            boolean success = dataFolder.mkdirs();
            if ( !success ) {
                getLogger().warning( "Could not create data folder" );
                Bukkit.getPluginManager().disablePlugin( this );
                return;
            }
        }

        // Connect to database
        database = new SQLiteDatabase();
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration( "database" );
        if ( !database.connect( databaseConfiguration ) ) {
            getLogger().warning( "Could not connect to database." );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }
        getLogger().info( "Successfully connected to database." );

    }

    @Override
    public void onDisable() {
        if ( database != null ) {
            database.disconnect();
        }

        getLogger().info( "Plugin disabled." );
    }

    public static Plugin getInstance() {
        return JavaPlugin.getPlugin( Plugin.class );
    }
}
