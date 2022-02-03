package de.rwth_aachen.cs_dc_mc.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class Bank {

    public static final Material CURRENCY_MATERIAL = Material.DIAMOND;
    public static final char CURRENCY_SYMBOL = '€';

    protected final BankDAO bankDAO;

    public Bank( BankDAO bankDAO ) {
        this.bankDAO = bankDAO;
    }

    public void tryCreateAccount( Player player ) {
        bankDAO.createBankAccount( player.getUniqueId() ).thenAccept( success -> {
            if ( !success ) {
                player.sendMessage( "§cCould not set up bank account" );
            }
        } );
    }

    public void tryRetrieveBalance( Player player ) {
        bankDAO.getBankAccount( player.getUniqueId() ).thenAccept( account -> {
            if ( account == null ) {
                player.sendMessage( "§cCould not retrieve balance" );
            } else {
                long whole = account.cents() / 100;
                long parts = account.cents() % 100;
                player.sendMessage( String.format( "§aBalance§7: §6%d.%02d %c", whole, parts, CURRENCY_SYMBOL ) );
            }
        } );
    }

    public void tryDeposit( Player player, int euros ) {
        if ( euros <= 0 ) {
            player.sendMessage( "§cNumber must be positive." );
            return;
        }

        boolean enoughItems = tryRemoveItems( player, CURRENCY_MATERIAL, euros );
        if ( !enoughItems ) {
            player.sendMessage( "§cYou dont have enough items to deposit!" );
            return;
        }

        player.sendMessage( String.format( "§7Initiating deposit of %d.00 %c", euros, CURRENCY_SYMBOL ) );

        bankDAO.deposit( player.getUniqueId(), euros * 100L )
                .thenAccept( success -> {
                    if ( success ) {
                        player.sendMessage( "§aDeposit successful" );
                    } else {
                        player.sendMessage( "§cDeposit unsuccessful" );
                        player.getInventory().addItem( new ItemStack( CURRENCY_MATERIAL, euros ) );
                    }
                } );
    }

    public void tryWithdraw( Player player, int euros ) {
        if ( euros <= 0 ) {
            player.sendMessage( "§cNumber must be positive." );
            return;
        }

        bankDAO.getBankAccount( player.getUniqueId() )
                .thenApply( account -> {
                    if ( account == null ) {
                        player.sendMessage( "§cCould not retrieve balance." );
                        throw new RuntimeException();
                    }
                    return account;
                } )
                .thenAccept( account -> {
                    long availableEuros = account.cents() / 100;
                    if ( euros > availableEuros ) {
                        player.sendMessage( "§cNot enough balance." );
                        throw new RuntimeException();
                    }
                } )
                .thenCompose( v -> bankDAO.withdraw( player.getUniqueId(), euros * 100L ) )
                .thenAccept( success -> {
                    if ( success ) {
                        player.getInventory().addItem( new ItemStack( CURRENCY_MATERIAL, euros ) );
                        player.sendMessage( "§aWithdraw successful." );
                    } else {
                        player.sendMessage( "§cWithdraw unsuccessful." );
                    }
                } );
    }

    private boolean tryRemoveItems( Player player, Material material, long number ) {
        Map<Integer, ? extends ItemStack> map = player.getInventory().all( material );
        int sum = 0;
        for ( Map.Entry<Integer, ? extends ItemStack> entry : map.entrySet() ) {
            if ( entry.getValue().getAmount() != 0 ) {
                sum += entry.getValue().getAmount();
            }
        }

        if ( sum < number ) {
            return false;
        }

        for ( Map.Entry<Integer, ? extends ItemStack> entry : map.entrySet() ) {
            if ( number == 0 ) {
                break;
            }

            if ( entry.getValue().getAmount() != 0 ) {
                if ( entry.getValue().getAmount() == 1 ) {
                    player.getInventory().setItem( entry.getKey(), null );
                    number -= 1;
                } else if ( entry.getValue().getAmount() > 1 ) {
                    int min = (int) Math.min( entry.getValue().getAmount(), number );
                    entry.getValue().setAmount( entry.getValue().getAmount() - min );
                    player.getInventory().setItem( entry.getKey(), entry.getValue() );
                    number -= min;
                }
            }
        }
        return true;
    }
}
