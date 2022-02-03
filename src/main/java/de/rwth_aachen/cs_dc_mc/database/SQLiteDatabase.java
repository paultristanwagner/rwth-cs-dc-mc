package de.rwth_aachen.cs_dc_mc.database;

import de.rwth_aachen.cs_dc_mc.DatabaseConfiguration;
import de.rwth_aachen.cs_dc_mc.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class SQLiteDatabase extends SQLDatabase {

    private static final String SQLITE_URL_BASE = "jdbc:sqlite:plugins/%s/%s.db";

    @Override
    public boolean connect( DatabaseConfiguration config ) {
        String dataFolderName = Plugin.getInstance().getDataFolder().getName();
        String url = String.format( SQLITE_URL_BASE, dataFolderName, config.database() );

        try {
            connection = DriverManager.getConnection( url, config.username(), config.password() );
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
