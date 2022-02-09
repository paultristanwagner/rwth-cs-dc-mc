package de.rwth_aachen.cs_dc_mc.gui;

import de.rwth_aachen.cs_dc_mc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public abstract class LoadingGUI<T> extends InventoryGUI {

    private static final ItemStack LOADING = ItemBuilder.of( Material.GRAY_STAINED_GLASS_PANE ).name( "§7Loading..." ).build();

    protected T t;

    public void open( Player player ) {
        this.player = player;
        inventory = createInventory();
        fillInventory();

        CompletableFuture.supplyAsync(
                this::load
        ).thenApply(
                data -> {
                    this.t = data;
                    return data;
                }
        ).thenAccept(
                this::doneLoading
        );

        player.openInventory( inventory );

        registerListener();
    }

    @Override
    protected void fillInventory() {
        int center = inventory.getSize() / 2 - 5;
        inventory.setItem( center, LOADING );
    }

    protected abstract T load();

    protected abstract void doneLoading( T t );
}
