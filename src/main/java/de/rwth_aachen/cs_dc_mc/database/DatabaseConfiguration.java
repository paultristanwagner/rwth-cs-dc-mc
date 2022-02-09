package de.rwth_aachen.cs_dc_mc.database;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public record DatabaseConfiguration(String database, String username, String password) {

    public DatabaseConfiguration(String database) {
        this(database, "", "");
    }
}
