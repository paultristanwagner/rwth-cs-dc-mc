package de.rwth_aachen.cs_dc_mc.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public abstract class SQLDataSource {

    public static final int CONSTRAINT_FAILED = 19;

    /**
     * Initializes the datasource
     *
     * @param databaseConfiguration the configuration
     * @return whether the datasource could be initialized
     */
    public abstract boolean initialize( DatabaseConfiguration databaseConfiguration );

    /**
     * Opens a connection to a database.
     *
     * @return the connection
     * @throws SQLException when the connection could not be established
     */
    public abstract Connection getConnection() throws SQLException;
}
