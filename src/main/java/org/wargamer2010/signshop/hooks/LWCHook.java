package org.wargamer2010.signshop.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class LWCHook implements Hook {

    @Override
    public String getName() {
        return "LWC";
    }

    @Override
    public Boolean canBuild(Player player, Block block) {
        if(HookManager.getHook("LWC") == null)
            return true;
        try {
            Object plugin = HookManager.getHook("LWC");
            Method getLWC = plugin.getClass().getMethod("getLWC");
            Object lwc = getLWC.invoke(plugin);
            if (lwc != null) {
                Method findProtection = lwc.getClass().getMethod("findProtection", Block.class);
                Object prot = findProtection.invoke(lwc, block);
                if (prot != null) {
                    Method canAccessProtection = lwc.getClass().getMethod("canAccessProtection", Player.class, Block.class);
                    Object res = canAccessProtection.invoke(lwc, player, block);
                    return (res instanceof Boolean) ? (Boolean) res : true;
                }
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    @Override
    public Boolean protectBlock(Player player, Block block) {
        return false;
    }
}
