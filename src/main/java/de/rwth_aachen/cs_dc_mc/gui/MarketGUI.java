package de.rwth_aachen.cs_dc_mc.gui;

import de.rwth_aachen.cs_dc_mc.Plugin;
import de.rwth_aachen.cs_dc_mc.economy.Offer;
import de.rwth_aachen.cs_dc_mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class MarketGUI extends LoadingGUI<List<Offer>> {

    private static final int CREATE_OFFER_SLOT = 53;
    private static final ItemStack CREATE_OFFER = ItemBuilder.of( Material.EMERALD_BLOCK ).name( "§aCreate offer" ).build();

    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 46;

    private static final ItemStack PREV_PAGE = ItemBuilder.of( Material.SUNFLOWER ).name( "§e« Previous page «" ).build();
    private static final ItemStack NEXT_PAGE = ItemBuilder.of( Material.SUNFLOWER ).name( "§e» Next page »" ).build();

    private static final int PAGE_SLOT = 49;
    private static final ItemStack PAGE_BASE = ItemBuilder.of( Material.REDSTONE_TORCH ).build();

    protected final int page;
    protected int lastPage;

    public MarketGUI() {
        this( 0 );
    }

    public MarketGUI( int page ) {
        this.page = page;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory( null, 54, "§aMarket" );
    }

    @Override
    public void onClick( InventoryClickEvent event ) {
        int slot = event.getSlot();
        if ( event.getInventory() == inventory ) {
            event.setCancelled( true );

            if ( slot == PREV_PAGE_SLOT && page != 0 ) {
                new MarketGUI( page - 1 ).open( player );
            } else if ( slot == NEXT_PAGE_SLOT && page < lastPage ) {
                new MarketGUI( page + 1 ).open( player );
            } else if ( slot == CREATE_OFFER_SLOT ) {
                new CreateOfferGUI().open( player );
            } else if ( slot >= 0 && slot < inventory.getSize() - 9 && slot < t.size() ) {
                int index = slot + page * ( inventory.getSize() - 9 );
                Offer offer = t.get( index );
                new AcceptOfferGUI( offer ).open( player );
            }
        }
    }

    @Override
    protected List<Offer> load() {
        return Plugin.getInstance().getMarketDAO().allOffers();
    }

    @Override
    protected void doneLoading( List<Offer> offers ) {
        inventory.clear();
        lastPage = t.size() / ( inventory.getSize() - 9 );

        int startIndex = page * ( inventory.getSize() - 9 );
        int endIndex = Math.min( ( page + 1 ) * ( inventory.getSize() - 9 ), offers.size() );
        int size = endIndex - startIndex;

        for ( int i = 0; i < size; i++ ) {
            int index = startIndex + i;
            Offer offer = offers.get( index );

            inventory.setItem( i, offer.toMarketItem() );
        }

        inventory.setItem( PREV_PAGE_SLOT, PREV_PAGE );
        inventory.setItem( NEXT_PAGE_SLOT, NEXT_PAGE );

        inventory.setItem( CREATE_OFFER_SLOT, CREATE_OFFER );

        ItemStack pageItem = ItemBuilder.of( PAGE_BASE ).name( "§e" + ( page + 1 ) + "§7/§e" + ( lastPage + 1 ) ).build();
        inventory.setItem( PAGE_SLOT, pageItem );
    }
}
