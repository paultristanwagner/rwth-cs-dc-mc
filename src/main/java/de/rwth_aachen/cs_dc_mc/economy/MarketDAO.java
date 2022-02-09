package de.rwth_aachen.cs_dc_mc.economy;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public interface MarketDAO {

    boolean setup();

    List<Offer> allOffers();

    Offer createOffer( UUID seller, int amount, long price, ItemStack item );

    boolean acceptOffer( Offer offer, UUID buyerId, int amount );

    boolean deleteOffer( UUID offerId );
}
