package de.rwth_aachen.cs_dc_mc.database;

import de.rwth_aachen.cs_dc_mc.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public abstract class SQLDatabase {

    protected Connection connection;

    /**
     * Tries to open a connection to a database.
     *
     * @param databaseConfiguration the configuration
     * @return whether the connection could be established.
     */
    public abstract boolean connect( DatabaseConfiguration databaseConfiguration );

    public void disconnect() {
        if ( connection == null ) {
            return;
        }

        try {
            connection.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement prepare( String statement ) {
        try {
            return connection.prepareStatement( statement );
        } catch ( SQLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean execute( PreparedStatement preparedStatement ) {
        try {
            return preparedStatement.execute();
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    public int executeUpdate( PreparedStatement preparedStatement ) {
        try {
            return preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            e.printStackTrace();
            return -1;
        }
    }

    public CompletableFuture<Boolean> executeAsync( PreparedStatement preparedStatement ) {
        return CompletableFuture.supplyAsync( () -> execute( preparedStatement ) );
    }

    public ResultSet query( PreparedStatement preparedStatement ) {
        try {
            return preparedStatement.executeQuery();
        } catch ( SQLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<ResultSet> queryAsync( PreparedStatement preparedStatement ) {
        return CompletableFuture.supplyAsync( () -> query( preparedStatement ) );
    }
}
