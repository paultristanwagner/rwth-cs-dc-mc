package de.rwth_aachen.cs_dc_mc.economy;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Paul Tristan Wagner <paultristanwagner@gmail.com>
 * @version 1.0
 */
public interface BankDAO {

    boolean setup();

    CompletableFuture<Boolean> createBankAccount( UUID uuid );

    CompletableFuture<BankAccount> getBankAccount( UUID uuid );

    CompletableFuture<Boolean> deposit( UUID uuid, Long cents );

    CompletableFuture<Boolean> withdraw( UUID uuid, Long cents );

    CompletableFuture<Boolean> transfer( UUID fromUUID, UUID toUUID, Long cents );
}
