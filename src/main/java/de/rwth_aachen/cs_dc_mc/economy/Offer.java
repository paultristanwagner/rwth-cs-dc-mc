package de.rwth_aachen.cs_dc_mc.economy;

import de.rwth_aachen.cs_dc_mc.util.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public record Offer(UUID offerId, UUID sellerId, int amount, long price, ItemStack item) {

    public ItemStack toMarketItem() {
        long whole = price() / 100;
        long parts = price() % 100;
        String priceFormat = String.format( "%d.%02d €", whole, parts );

        return ItemBuilder.of( item() )
                .lore( "", "§6Price§7: §a" + priceFormat, "§6In stock§7: §b" + amount() )
                .build();
    }
}
