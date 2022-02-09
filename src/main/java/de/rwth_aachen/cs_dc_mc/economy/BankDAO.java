package de.rwth_aachen.cs_dc_mc.economy;

import java.util.UUID;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public interface BankDAO {

    boolean setup();

    boolean createBankAccount( UUID uuid );

    BankAccount getBankAccount( UUID uuid );

    boolean deposit( UUID uuid, Long cents );

    boolean withdraw( UUID uuid, Long cents );

    boolean transfer( UUID fromUUID, UUID toUUID, Long cents );
}
