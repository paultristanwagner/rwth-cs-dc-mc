package de.rwth_aachen.cs_dc_mc;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info( "Plugin enabled." );
    }

    @Override
    public void onDisable() {
        getLogger().info( "Plugin disabled." );
    }

    public static Plugin getInstance() {
        return JavaPlugin.getPlugin( Plugin.class );
    }
}
