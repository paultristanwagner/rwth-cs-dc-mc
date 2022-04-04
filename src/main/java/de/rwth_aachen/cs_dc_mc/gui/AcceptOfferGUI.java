package de.rwth_aachen.cs_dc_mc.gui;

import de.rwth_aachen.cs_dc_mc.Plugin;
import de.rwth_aachen.cs_dc_mc.economy.Offer;
import de.rwth_aachen.cs_dc_mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class AcceptOfferGUI extends InventoryGUI {
    
    private static final int ACCEPT_SLOT = 11;
    private static final ItemStack ACCEPT = ItemBuilder.of( Material.EMERALD_BLOCK ).name( "§aAccept" ).build();
    
    private static final int ITEM_SLOT = 13;
    
    private static final int CANCEL_SLOT = 15;
    private static final ItemStack CANCEL = ItemBuilder.of( Material.REDSTONE_BLOCK ).name( "§cCancel" ).build();
    
    private static final int DECREASE_SLOT = 30;
    private static final ItemStack DECREASE = ItemBuilder.of( Material.OAK_BUTTON ).name( "§e-1" ).build();
    private static final int AMOUNT_SLOT = 31;
    private static final ItemStack AMOUNT_BASE = ItemBuilder.of( Material.PAPER ).build();
    private static final int INCREASE_SLOT = 32;
    private static final ItemStack INCREASE = ItemBuilder.of( Material.OAK_BUTTON ).name( "§e+1" ).build();
    
    private final Offer offer;
    private int amount;
    
    public AcceptOfferGUI( Offer offer ) {
        this.offer = offer;
        this.amount = 1;
    }
    
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory( null, 45, "§7Accept offer" );
    }
    
    @Override
    protected void fillInventory() {
        inventory.setItem( ACCEPT_SLOT, ACCEPT );
        inventory.setItem( CANCEL_SLOT, CANCEL );
        
        inventory.setItem( ITEM_SLOT, offer.toMarketItem() );
        
        displayAmount();
    }
    
    protected void displayAmount() {
        if ( amount > 1 ) {
            inventory.setItem( DECREASE_SLOT, DECREASE );
        } else {
            inventory.setItem( DECREASE_SLOT, null );
        }
        
        if ( amount < offer.amount() ) {
            inventory.setItem( INCREASE_SLOT, INCREASE );
        } else {
            inventory.setItem( INCREASE_SLOT, null );
        }
        
        ItemStack amountItem = ItemBuilder.of( AMOUNT_BASE ).name( "§eAmount§7: §b" + amount ).build();
        inventory.setItem( AMOUNT_SLOT, amountItem );
    }
    
    @Override
    public void onClick( InventoryClickEvent event ) {
        event.setCancelled( true );
        
        if ( event.getAction() == InventoryAction.NOTHING ) {
            return;
        }
        
        int slot = event.getSlot();
        if ( slot == ACCEPT_SLOT ) {
            acceptOffer();
        } else if ( slot == CANCEL_SLOT ) {
            player.closeInventory();
        } else if ( slot == DECREASE_SLOT ) {
            amount = Math.max( amount - 1, 0 );
            displayAmount();
        } else if ( slot == INCREASE_SLOT ) {
            amount = Math.min( amount + 1, offer.amount() );
            displayAmount();
        }
    }
    
    protected void acceptOffer() {
        UUID uuid = player.getUniqueId();
        long payment = amount * offer.price();
        
        CompletableFuture.supplyAsync(
                () -> Plugin.getInstance().getBankDAO().getBankAccount( uuid )
        ).thenApply(
                bankAccount -> {
                    if ( bankAccount != null && bankAccount.cents() < payment ) {
                        // todo: this causes different behavior if you buy from yourself anyway (if you dont have enough money you get an error even though the transfer would not change anything)
                        player.sendMessage( "§cYou don't have enough money." );
                        throw new RuntimeException();
                    }
                    return null;
                }
        ).thenApplyAsync(
                v -> Plugin.getInstance().getMarketDAO().acceptOffer( offer, uuid, amount )
        ).thenAccept(
                success -> {
                    if ( success ) {
                        ItemStack item = ItemBuilder.of( offer.item() ).amount( amount ).build();
                        player.getInventory().addItem( item );
                        long whole = payment / 100;
                        long parts = payment % 100;
                        player.sendMessage( String.format( "§aSuccess§7: §7Bought §e%dx §7for a total of §b%d.%02d §e€", amount, whole, parts ) );
                    } else {
                        player.sendMessage( "§cCould not accept offer." );
                    }
    
                    new MarketGUI().open( player );
                }
        );
    }
}
