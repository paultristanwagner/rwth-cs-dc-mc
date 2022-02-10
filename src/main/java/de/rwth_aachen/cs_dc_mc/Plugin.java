package de.rwth_aachen.cs_dc_mc;

import de.rwth_aachen.cs_dc_mc.command.BankCommand;
import de.rwth_aachen.cs_dc_mc.command.MarketCommand;
import de.rwth_aachen.cs_dc_mc.database.*;
import de.rwth_aachen.cs_dc_mc.economy.Bank;
import de.rwth_aachen.cs_dc_mc.economy.BankDAO;
import de.rwth_aachen.cs_dc_mc.economy.MarketDAO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Random;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class Plugin extends JavaPlugin {

    private SQLDataSource dataSource;

    private BankDAO bankDAO;
    private Bank bank;

    private MarketDAO marketDAO;

    @Override
    public void onEnable() {
        getLogger().info( "Plugin enabled." );

        // Create the data folder
        File dataFolder = getDataFolder();
        if ( !dataFolder.exists() ) {
            boolean success = dataFolder.mkdirs();
            if ( !success ) {
                getLogger().warning( "Could not create data folder" );
                Bukkit.getPluginManager().disablePlugin( this );
                return;
            }
        }

        // Initialize data source
        dataSource = new SQLiteDataSource();
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration( "database" );
        if ( !dataSource.initialize( databaseConfiguration ) ) {
            getLogger().warning( "Could not initialize data source." );
            Bukkit.getPluginManager().disablePlugin( this );
            return;
        }
        getLogger().info( "Successfully initialized data source." );

        // Setup bank
        bankDAO = new SQLBankDAO( dataSource );
        bank = new Bank( bankDAO );
        boolean bankSuccess = bankDAO.setup();
        if ( bankSuccess ) {
            getLogger().info( "Set up the bank." );
        }

        getCommand( "bank" ).setExecutor( new BankCommand() );

        // Setup market
        marketDAO = new SQLMarketDAO( dataSource );
        boolean marketSuccess = marketDAO.setup();
        if ( marketSuccess ) {
            getLogger().info( "Set up the market." );
        }

        for ( int i = 0; i < 217; i++ ) { // todo
            Material material = Material.values()[rdm( 0, Material.values().length )];
            if ( !material.isItem() ) {
                continue;
            }
            // marketDAO.createOffer( UUID.randomUUID(), rdm( 1, 64 ), rdm( 1, 70 ) * 50L, new ItemStack( material ) );
        }

        getCommand( "market" ).setExecutor( new MarketCommand() );
    }

    private int rdm( int from, int to ) { // todo
        Random random = new Random();
        return random.nextInt( to - from ) + from;
    }

    @Override
    public void onDisable() {
        getLogger().info( "Plugin disabled." );
    }

    public static Plugin getInstance() {
        return JavaPlugin.getPlugin( Plugin.class );
    }

    public Bank getBank() {
        return bank;
    }

    public BankDAO getBankDAO() {
        return bankDAO;
    }

    public MarketDAO getMarketDAO() {
        return marketDAO;
    }
}
