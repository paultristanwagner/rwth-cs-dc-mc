package de.rwth_aachen.cs_dc_mc.gui;

import de.rwth_aachen.cs_dc_mc.Plugin;
import de.rwth_aachen.cs_dc_mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class CreateOfferGUI extends InventoryGUI {

    private static final int INPUT_SLOT = 20;
    private static final int CONFIRM_SLOT = 32;
    private static final int CANCEL_SLOT = 34;

    private static final int INCREASE_PRICE_SLOT = 16;
    private static final int PRICE_SLOT = 15;
    private static final int DECREASE_PRICE_SLOT = 14;


    private static final ItemStack BOX = ItemBuilder.of( Material.BLACK_STAINED_GLASS_PANE ).name( "§7Enter the item you want to offer" ).build();
    private static final ItemStack CREATE = ItemBuilder.of( Material.EMERALD_BLOCK ).name( "§aCreate offer" ).build();
    private static final ItemStack CANCEL = ItemBuilder.of( Material.REDSTONE_BLOCK ).name( "§cCancel" ).build();

    private static final ItemStack PRICE_BASE = ItemBuilder.of( Material.DIAMOND ).build();
    private static final ItemStack INCREASE_PRICE = ItemBuilder.of( Material.OAK_BUTTON ).name( "§e+0.10 €" ).build();
    private static final ItemStack DECREASE_PRICE = ItemBuilder.of( Material.OAK_BUTTON ).name( "§e-0.10 €" ).build();

    protected long price = 100;

    protected boolean confirmed = false;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory( null, 45, "§7Create offer" );
    }

    @Override
    protected void fillInventory() {
        inventory.setItem( 10, BOX );
        inventory.setItem( 11, BOX );
        inventory.setItem( 12, BOX );

        inventory.setItem( 19, BOX );
        inventory.setItem( 21, BOX );

        inventory.setItem( 28, BOX );
        inventory.setItem( 29, BOX );
        inventory.setItem( 30, BOX );

        inventory.setItem( 32, CREATE );
        inventory.setItem( 34, CANCEL );

        displayPrice();
    }

    @Override
    public void onClick( InventoryClickEvent event ) {
        int slot = event.getSlot();

        if ( event.getInventory() == inventory && event.getClickedInventory() != inventory && event.isShiftClick() ) {
            event.setCancelled( true );
            return;
        }

        if ( event.getClickedInventory() == inventory && slot != INPUT_SLOT ) {
            event.setCancelled( true );

            if ( slot == CONFIRM_SLOT ) {
                confirm();
            } else if ( slot == CANCEL_SLOT ) {
                player.closeInventory();
            } else if ( slot == DECREASE_PRICE_SLOT ) {
                price = Math.max( price - 10, 0 );
                displayPrice();
            } else if ( slot == INCREASE_PRICE_SLOT ) {
                price += 10;
                displayPrice();
            }

        }
    }

    @Override
    public void onClose( InventoryCloseEvent event ) {
        if ( !confirmed ) {
            ItemStack item = inventory.getItem( INPUT_SLOT );
            if ( item != null ) {
                player.getInventory().addItem( item );
            }
        }
    }

    protected void confirm() {
        confirmed = true;

        ItemStack offeredItem = inventory.getItem( INPUT_SLOT );
        if ( offeredItem != null ) {
            int amount = offeredItem.getAmount();
            player.closeInventory();

            ItemStack singleItem = ItemBuilder.of( offeredItem ).amount( 1 ).build();

            CompletableFuture.supplyAsync(
                    () -> Plugin.getInstance().getMarketDAO().createOffer( player.getUniqueId(), amount, price, singleItem )
            ).thenAccept(
                    offer -> {
                        if ( offer == null ) {
                            player.sendMessage( "§cCould not create offer." );
                            player.getInventory().addItem( offeredItem );
                        } else {
                            player.sendMessage( "§aSuccessfully created offer." );
                        }
                    }
            );
        }
    }

    protected void displayPrice() {
        if ( price >= 10 ) {
            inventory.setItem( DECREASE_PRICE_SLOT, DECREASE_PRICE );
        } else {
            inventory.setItem( DECREASE_PRICE_SLOT, null );
        }

        if ( price <= Long.MAX_VALUE - 10 ) {
            inventory.setItem( INCREASE_PRICE_SLOT, INCREASE_PRICE );
        } else {
            inventory.setItem( INCREASE_PRICE_SLOT, null );
        }

        String priceString = String.format( "§ePrice§7: §b%d.%02d €", price / 100, price % 100 );
        ItemStack priceItem = ItemBuilder.of( PRICE_BASE ).name( priceString ).build();
        inventory.setItem( PRICE_SLOT, priceItem );
    }
}
