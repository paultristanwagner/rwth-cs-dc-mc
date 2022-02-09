package de.rwth_aachen.cs_dc_mc.database;

import de.rwth_aachen.cs_dc_mc.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class SQLiteDataSource extends SQLDataSource {

    private static final String SQLITE_URL_BASE = "jdbc:sqlite:plugins/%s/%s.db";

    protected String url;

    @Override
    public boolean initialize( DatabaseConfiguration config ) {
        String dataFolderName = Plugin.getInstance().getDataFolder().getName();
        url = String.format( SQLITE_URL_BASE, dataFolderName, config.database() );

        return true;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection( url );
        connection.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
        return connection;
    }
}
