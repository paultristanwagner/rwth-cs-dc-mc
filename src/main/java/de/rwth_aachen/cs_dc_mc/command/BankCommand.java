package de.rwth_aachen.cs_dc_mc.command;

import de.rwth_aachen.cs_dc_mc.Plugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if ( !( sender instanceof Player ) ) {
            sender.sendMessage( "This command can only be executed by players." );
            return true;
        }
        Player player = (Player) sender;
        Plugin.getInstance().getBank().tryCreateAccount( player );

        if ( args.length == 0 ) {
            Plugin.getInstance().getBank().tryRetrieveBalance( player );
        } else if ( args.length == 2 ) {
            int amount;
            try {
                amount = Integer.parseInt( args[1] );
            } catch ( NumberFormatException e ) {
                player.sendMessage( "§cEnter a valid number!" );
                return true;
            }

            if ( args[0].equalsIgnoreCase( "deposit" ) ) {
                Plugin.getInstance().getBank().tryDeposit( player, amount );
            } else if ( args[0].equalsIgnoreCase( "withdraw" ) ) {
                Plugin.getInstance().getBank().tryWithdraw( player, amount );
            }
        }
        return true;
    }
}
