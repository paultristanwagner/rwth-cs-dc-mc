package de.rwth_aachen.cs_dc_mc.economy;

import de.rwth_aachen.cs_dc_mc.database.SQLDatabase;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class SQLBankDAO implements BankDAO {

    protected SQLDatabase database;

    public SQLBankDAO( SQLDatabase database ) {
        this.database = database;
    }

    @Override
    public boolean setup() {
        PreparedStatement ps = database.prepare(
                "CREATE TABLE IF NOT EXISTS bankAccount(" +
                        "uuid VARCHAR(36) NOT NULL, " +
                        "cents LONG DEFAULT 0, " +
                        "PRIMARY KEY (uuid)" +
                        ");"
        );
        database.execute( ps );
        return true;
    }

    @Override
    public CompletableFuture<Boolean> createBankAccount( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement ps = database.prepare( "INSERT OR IGNORE INTO bankAccount(uuid) VALUES (?);" ) ) {
                ps.setString( 1, uuid.toString() );
                return database.executeUpdate( ps ) >= 0;
            } catch ( SQLException e ) {
                e.printStackTrace();
                return null;
            }
        } );
    }

    @Override
    public CompletableFuture<BankAccount> getBankAccount( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement ps = database.prepare( "SELECT * FROM bankAccount WHERE uuid = ?;" ) ) {
                ps.setString( 1, uuid.toString() );
                ResultSet resultSet = database.query( ps );
                long cents = resultSet.getLong( "cents" );
                return new BankAccount( uuid, cents );
            } catch ( SQLException e ) {
                e.printStackTrace();
                return null;
            }
        } );
    }

    @Override
    public CompletableFuture<Boolean> deposit( UUID uuid, Long cents ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement ps = database.prepare( "UPDATE bankAccount SET cents = cents + ? WHERE uuid = ?;" ) ) {
                ps.setLong( 1, cents );
                ps.setString( 2, uuid.toString() );
                return database.executeUpdate( ps ) > 0;
            } catch ( SQLException e ) {
                e.printStackTrace();
                return false;
            }
        } );
    }

    @Override
    public CompletableFuture<Boolean> withdraw( UUID uuid, Long cents ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement ps = database.prepare( "UPDATE bankAccount SET cents = cents - ? WHERE uuid = ?;" ) ) {
                ps.setLong( 1, cents );
                ps.setString( 2, uuid.toString() );
                return database.executeUpdate( ps ) > 0;
            } catch ( SQLException e ) {
                e.printStackTrace();
                return false;
            }
        } );
    }

    @Override
    public CompletableFuture<Boolean> transfer( UUID fromUUID, UUID toUUID, Long cents ) {
        throw new UnsupportedOperationException();
    }
}
