package de.rwth_aachen.cs_dc_mc.economy;

import de.rwth_aachen.cs_dc_mc.database.SQLDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class SQLBankDAO implements BankDAO {

    protected SQLDataSource dataSource;

    public SQLBankDAO( SQLDataSource dataSource ) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean setup() {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS bankAccount(" +
                                "uuid VARCHAR(36) NOT NULL, " +
                                "cents LONG DEFAULT 0, " +
                                "PRIMARY KEY (uuid)," +
                                "CHECK (cents >= 0)" +
                                ");"
                )
        ) {
            ps.executeUpdate();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createBankAccount( UUID uuid ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "INSERT OR IGNORE INTO bankAccount(uuid) VALUES (?);" )
        ) {
            ps.setString( 1, uuid.toString() );
            return ps.executeUpdate() >= 0;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public BankAccount getBankAccount( UUID uuid ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "SELECT * FROM bankAccount WHERE uuid = ?;" )
        ) {
            ps.setString( 1, uuid.toString() );
            ResultSet resultSet = ps.executeQuery();
            long cents = resultSet.getLong( "cents" );
            return new BankAccount( uuid, cents );
        } catch ( SQLException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deposit( UUID uuid, Long cents ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "UPDATE bankAccount SET cents = cents + ? WHERE uuid = ?;" )
        ) {
            ps.setLong( 1, cents );
            ps.setString( 2, uuid.toString() );
            return ps.executeUpdate() > 0;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean withdraw( UUID uuid, Long cents ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "UPDATE bankAccount SET cents = cents - ? WHERE uuid = ?;" )
        ) {
            ps.setLong( 1, cents );
            ps.setString( 2, uuid.toString() );
            return ps.executeUpdate() > 0;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean transfer( UUID fromUUID, UUID toUUID, Long cents ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement decrease = con.prepareStatement( "UPDATE bankAccount SET cents = cents - ? WHERE uuid = ?;" );
                PreparedStatement increase = con.prepareStatement( "UPDATE bankAccount SET cents = cents + ? WHERE uuid = ?;" )
        ) {
            con.setAutoCommit( false );

            decrease.setLong( 1, cents );
            decrease.setString( 2, fromUUID.toString() );

            increase.setLong( 1, cents );
            increase.setString( 2, toUUID.toString() );

            int uc0 = decrease.executeUpdate();
            int uc1 = increase.executeUpdate();

            if ( uc0 > 0 && uc1 > 0 ) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }
}
