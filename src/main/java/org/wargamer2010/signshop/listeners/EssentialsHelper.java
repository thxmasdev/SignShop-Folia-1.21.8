
package org.wargamer2010.signshop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.util.signshopUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;

class EssentialsHelper {
    private static boolean essConflictFound = false;

    private EssentialsHelper() {

    }

    protected static boolean isEssentialsConflictFound() {
        return EssentialsHelper.essConflictFound;
    }

    protected static void essentialsCheck(Plugin plugin) {
        if(plugin == null)
            return;
        try {
            Method getSettings = plugin.getClass().getMethod("getSettings");
            Object settings = getSettings.invoke(plugin);
            if (settings == null) return;
            Method areSignsDisabled = settings.getClass().getMethod("areSignsDisabled");
            Object disabled = areSignsDisabled.invoke(settings);
            if (disabled instanceof Boolean && !(Boolean) disabled) {
                SignShop.log("Essentials signs are enabled, checking for conflicts now!", Level.WARNING);
                SignShop.log("Even if no conflicts are found, it is recommended to disable all of Essentials signs including -color!", Level.WARNING);
                Method enabledSigns = settings.getClass().getMethod("enabledSigns");
                Object list = enabledSigns.invoke(settings);
                essConflictFound = false;
                if (list instanceof Collection) {
                    for (Object sign : (Collection<?>) list) {
                        Method getTemplateName = sign.getClass().getMethod("getTemplateName");
                        Object nameObj = getTemplateName.invoke(sign);
                        String name = String.valueOf(nameObj);
                        String essShopName = signshopUtil.getOperation(name);
                        if(essShopName.isEmpty())
                            continue;
                        essShopName = ChatColor.stripColor(essShopName).toLowerCase();
                        if (!SignShop.getInstance().getSignShopConfig().getBlocks(essShopName).isEmpty()) {
                            SignShop.log("Sign with name " + name + " is enabled for Essentials and conflicts with SignShop!", Level.SEVERE);
                            if (!essConflictFound)
                                essConflictFound = true;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
