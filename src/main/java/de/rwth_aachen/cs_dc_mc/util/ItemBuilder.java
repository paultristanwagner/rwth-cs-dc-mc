package de.rwth_aachen.cs_dc_mc.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class ItemBuilder {

    protected final ItemStack itemStack;

    private Material material;
    private int amount;
    private short durability;
    protected final ItemMeta meta;

    protected ItemBuilder( ItemStack itemStack ) {
        this.itemStack = itemStack;

        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.durability = itemStack.getDurability();
        this.meta = itemStack.getItemMeta();
    }

    public ItemStack apply() {
        apply( itemStack );
        return itemStack;
    }

    public ItemStack build() {
        return apply( itemStack.clone() );
    }

    protected ItemStack apply( ItemStack targetItemStack ) {
        targetItemStack.setType( material );
        targetItemStack.setAmount( amount );
        targetItemStack.setDurability( durability );
        targetItemStack.setItemMeta( meta );
        return targetItemStack;
    }

    public ItemBuilder material( Material material ) {
        this.material = material;
        return this;
    }

    public Material material() {
        return material;
    }

    public ItemBuilder amount( int amount ) {
        this.amount = amount;
        return this;
    }

    public int amount() {
        return amount;
    }

    public ItemBuilder durability( short durability ) {
        this.durability = durability;
        return this;
    }

    public short durability() {
        return durability;
    }

    public ItemBuilder name( String displayName ) {
        meta.setDisplayName( displayName );
        return this;
    }

    public String name() {
        return meta.getDisplayName();
    }

    public ItemBuilder lore( List<String> lore ) {
        meta.setLore( lore );
        return this;
    }

    public ItemBuilder lore( String... lore ) {
        return lore( Arrays.asList( lore ) );
    }

    public List<String> lore() {
        return meta.getLore();
    }

    public ItemBuilder enchant( Enchantment enchantment, int level ) {
        meta.addEnchant( enchantment, level, true );
        return this;
    }

    public ItemBuilder disenchant( Enchantment enchantment ) {
        meta.removeEnchant( enchantment );
        return this;
    }

    public ItemBuilder addItemFlags( ItemFlag... hiddenFlags ) {
        meta.addItemFlags( hiddenFlags );
        return this;
    }

    public ItemBuilder removeItemFlags( ItemFlag... hiddenFlags ) {
        meta.removeItemFlags( hiddenFlags );
        return this;
    }

    public ItemBuilder unbreakable() {
        meta.setUnbreakable( true );
        return this;
    }

    public ItemBuilder glow() {
        glow( true );
        return this;
    }

    public ItemBuilder glow( boolean glow ) {
        if ( glow ) {
            enchant( Enchantment.LUCK, 1 );
            addItemFlags( ItemFlag.HIDE_ENCHANTS );
        } else {
            disenchant( Enchantment.LUCK );
            removeItemFlags( ItemFlag.HIDE_ENCHANTS );
        }

        return this;
    }

    public static ItemStack air() {
        return new ItemStack( Material.AIR );
    }

    public static ItemBuilder of( Material material ) {
        return new ItemBuilder( new ItemStack( material ) );
    }

    public static ItemBuilder of( ItemStack item ) {
        return new ItemBuilder( item );
    }
}
