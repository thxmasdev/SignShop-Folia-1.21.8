package org.wargamer2010.signshop.worth;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public class EssentialsWorthHandler implements WorthHandler {
    Object ess;
    Object worth;

    public EssentialsWorthHandler() {
        ess = Bukkit.getPluginManager().getPlugin("Essentials");
        try {
            if (ess != null) {
                Method getWorth = ess.getClass().getMethod("getWorth");
                worth = getWorth.invoke(ess);
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public double getPrice(ItemStack stack) {
        try {
            if (worth != null) {
                Method getPrice = worth.getClass().getMethod("getPrice", ess.getClass(), ItemStack.class);
                Object res = getPrice.invoke(worth, ess, stack);
                if (res instanceof BigDecimal) {
                    return ((BigDecimal) res).doubleValue();
                }
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }
}
