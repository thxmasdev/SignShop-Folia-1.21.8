package org.wargamer2010.signshop.worth;

import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.SignShop;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class CMIWorthHandler implements WorthHandler {
    private final Object worthManager;

    public CMIWorthHandler() {
        Object wm = null;
        try {
            Class<?> cmi = Class.forName("com.Zrips.CMI.CMI");
            Method getInstance = cmi.getMethod("getInstance");
            Object cmiInstance = getInstance.invoke(null);
            Method getWorthManager = cmiInstance.getClass().getMethod("getWorthManager");
            wm = getWorthManager.invoke(cmiInstance);
        } catch (Throwable ignored) {
        }
        worthManager = wm;
    }

    @Override
    public double getPrice(ItemStack stack) {
        double d = 0;
        try {
            if (worthManager != null) {
                Method getWorth = worthManager.getClass().getMethod("getWorth", ItemStack.class);
                Object worthItem = getWorth.invoke(worthManager, stack);
                if (worthItem != null) {
                    Method getSellPrice = worthItem.getClass().getMethod("getSellPrice");
                    Object price = getSellPrice.invoke(worthItem);
                    if (price instanceof Number) {
                        d = ((Number) price).doubleValue();
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        if (SignShop.getInstance().getSignShopConfig().debugging()) {
            SignShop.log(stack.getType() + " is worth " + d, Level.INFO);
        }
        return d;
    }
}
