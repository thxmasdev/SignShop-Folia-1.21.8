package org.wargamer2010.signshop.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.Vault;

import java.util.logging.Level;

public class ServerLoadedListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerLoaded(ServerLoadEvent event) {
        if (Vault.getEconomy() == null || !Vault.getEconomy().isEnabled()) {
            boolean economyHooked = SignShop.getInstance().getVault().setupEconomy();
            if (economyHooked) {
                SignShop.log("Vault economy successfully hooked!", Level.INFO);
            }
            else {
                SignShop.log("Could not hook into vault's economy. Make sure you have an economy plugin in addition to Vault", Level.WARNING);
            }
        }
        try {
            org.wargamer2010.signshop.configuration.Storage.get().processDeferredForLoadedWorlds();
        } catch (Throwable ignored) {}

        try {
            Bukkit.getScheduler().runTaskLater(SignShop.getInstance(), () -> {
                SignShop.log("Loaded " + org.wargamer2010.signshop.configuration.Storage.get().shopCount() + " valid shops (post-load).", Level.INFO);
            }, 20L);
        } catch (Throwable ignored) {}
    }
}
