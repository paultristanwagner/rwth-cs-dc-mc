package de.rwth_aachen.cs_dc_mc.gui;

import de.rwth_aachen.cs_dc_mc.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public abstract class InventoryGUI implements Listener {

    protected Player player;
    protected Inventory inventory;

    /**
     * Specifies the inventory that should be used.
     * @return the inventory
     */
    protected abstract Inventory createInventory();

    /**
     * Fills the inventory with its content
     */
    protected abstract void fillInventory();

    private void registerListener() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents( this, Plugin.getInstance() );
    }

    private void unregisterListener() {
        HandlerList.unregisterAll( this );
    }

    /**
     * Opens the GUI for a specific player
     * @param player the player
     */
    public void open( Player player ) {
        this.player = player;
        inventory = createInventory();
        fillInventory();

        player.openInventory( inventory );

        registerListener();
    }

    /**
     * Click even that gets called whenever the player interacts with the GUI
     * @param event the event
     */
    @EventHandler
    public void onClick( InventoryClickEvent event ) {

    }

    /**
     * Close event that gets called whenever the player closes the inventory
     * @param event the event
     */
    @EventHandler
    public void onClose( InventoryCloseEvent event ) {

    }

    @EventHandler( priority = EventPriority.HIGHEST, ignoreCancelled = false )
    public final void onCloseInternal( InventoryCloseEvent event ) {
        if ( event.getInventory().equals( inventory ) ) {
            unregisterListener();
        }
    }
}
