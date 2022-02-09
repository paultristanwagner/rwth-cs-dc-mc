package de.rwth_aachen.cs_dc_mc.command;

import de.rwth_aachen.cs_dc_mc.gui.MarketGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class MarketCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if ( !( sender instanceof Player ) ) {
            sender.sendMessage( "This command can only be used by players." );
            return true;
        }

        Player player = (Player) sender;
        new MarketGUI().open( player );
        return true;
    }
}
