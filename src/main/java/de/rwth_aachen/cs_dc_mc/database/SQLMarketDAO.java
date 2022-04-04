package de.rwth_aachen.cs_dc_mc.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.rwth_aachen.cs_dc_mc.economy.MarketDAO;
import de.rwth_aachen.cs_dc_mc.economy.Offer;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public class SQLMarketDAO implements MarketDAO {

    private SQLDataSource dataSource;

    public SQLMarketDAO( SQLDataSource dataSource ) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean setup() {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS offer(" +
                                "id VARCHAR(36) NOT NULL, " +
                                "sellerId VARCHAR(36) NOT NULL, " +
                                "amount INT NOT NULL, " +
                                "price LONG NOT NULL, " +
                                "item TEXT," +
                                "PRIMARY KEY (id)," +
                                "CHECK (price >= 0 AND amount >= 0)" +
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
    public List<Offer> allOffers() {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "SELECT * FROM offer;" )
        ) {
            ResultSet rs = ps.executeQuery();
            List<Offer> offers = new ArrayList<>();
            while ( rs.next() ) {
                Offer offer = offer( rs );
                offers.add( offer );
            }
            return offers;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Offer createOffer( UUID sellerId, int amount, long price, ItemStack item ) {
        UUID offerId = UUID.randomUUID();

        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "INSERT INTO offer(id, sellerId, amount, price, item) VALUES (?, ?, ?, ?, ?);" )
        ) {
            ps.setString( 1, offerId.toString() );
            ps.setString( 2, sellerId.toString() );
            ps.setInt( 3, amount );
            ps.setLong( 4, price );
            ps.setString( 5, item.serialize().toString() );

            int uc = ps.executeUpdate();
            if ( uc > 0 ) {
                return new Offer( offerId, sellerId, amount, price, item );
            }
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean acceptOffer( Offer offer, UUID buyerId, int amount ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement decreaseAmount = con.prepareStatement( "UPDATE offer SET amount = amount - ? WHERE id = ?" )
        ) {
            con.setAutoCommit( false );

            decreaseAmount.setLong( 1, amount );
            decreaseAmount.setString( 2, offer.offerId().toString() );

            try {
                int uc = decreaseAmount.executeUpdate();
                if ( uc <= 0 ) {
                    con.rollback();
                    return false;
                }
            } catch ( SQLException e ) {
                con.rollback();
                return false;
            }

            PreparedStatement deleteOffer = con.prepareStatement( "DELETE FROM offer WHERE id = ? AND amount = 0;" );
            deleteOffer.setString( 1, offer.offerId().toString() );

            deleteOffer.executeUpdate();

            PreparedStatement decreaseBalance = con.prepareStatement( "UPDATE bankAccount SET cents = cents - ? WHERE uuid = ?;" );
            PreparedStatement increaseBalance = con.prepareStatement( "UPDATE bankAccount SET cents = cents + ? WHERE uuid = ?;" );

            long payment = amount * offer.price();
            decreaseBalance.setLong( 1, payment );
            decreaseBalance.setString( 2, buyerId.toString() );
            increaseBalance.setLong( 1, payment );
            increaseBalance.setString( 2, offer.sellerId().toString() );

            try {
                int uc0 = decreaseBalance.executeUpdate();
                int uc1 = increaseBalance.executeUpdate();
                if ( uc0 <= 0 || uc1 <= 0 ) {
                    con.rollback();
                    return false;
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
                con.rollback();
                return false;
            }

            con.commit();
            return true;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteOffer( UUID offerId ) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement ps = con.prepareStatement( "DELETE FROM offer WHERE id = ?;" )
        ) {
            int uc = ps.executeUpdate();
            return uc > 0;
        } catch ( SQLException e ) {
            e.printStackTrace();
            return false;
        }
    }

    private Offer offer( ResultSet resultSet ) throws SQLException {
        String item = resultSet.getString( "item" );
        Map<String, Object> map = new Gson().fromJson( item, new TypeToken<Map<String, Object>>() {
        }.getType() );

        return new Offer(
                UUID.fromString( resultSet.getString( "id" ) ),
                UUID.fromString( resultSet.getString( "sellerId" ) ),
                resultSet.getInt( "amount" ),
                resultSet.getLong( "price" ),
                ItemStack.deserialize( map )
        );
    }
}
