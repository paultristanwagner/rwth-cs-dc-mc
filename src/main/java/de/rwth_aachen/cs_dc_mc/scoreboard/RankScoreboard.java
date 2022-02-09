package de.rwth_aachen.cs_dc_mc.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class RankScoreboard {

    private final Scoreboard scoreboard;

    public RankScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        register();
    }

    private void register() {
        Team contributorTeam = getOrCreateTeam( "0contributor", "§bContributor §7| ", ChatColor.AQUA );
        Team defaultTeam = getOrCreateTeam( "1default", "", ChatColor.GRAY );
    }

    private Team getOrCreateTeam( String name, String prefix, ChatColor color ) {
        Team team = scoreboard.getTeam( name );
        if(team != null) {
            return team;
        }

        team = scoreboard.registerNewTeam( name );
        team.setPrefix( prefix );
        team.setColor( color );

        return team;
    }
}
