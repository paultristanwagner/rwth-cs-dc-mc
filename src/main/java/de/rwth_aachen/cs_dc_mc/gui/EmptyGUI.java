package de.rwth_aachen.cs_dc_mc.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class EmptyGUI extends InventoryGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Empty GUI");
    }

    @Override
    protected void fillInventory() {

    }
}
