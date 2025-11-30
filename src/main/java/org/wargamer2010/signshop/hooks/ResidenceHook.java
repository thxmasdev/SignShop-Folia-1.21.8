package org.wargamer2010.signshop.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class ResidenceHook implements Hook {

    @Override
    public String getName() {
        return "Residence";
    }

    @Override
    public Boolean canBuild(Player player, Block block) {
        if (HookManager.getHook("Residence") == null)
            return true;
        try {
            Class<?> residenceCls = Class.forName("com.bekvon.bukkit.residence.Residence");
            Method getInstance = residenceCls.getMethod("getInstance");
            Object residence = getInstance.invoke(null);
            Method getResidenceManager = residence.getClass().getMethod("getResidenceManager");
            Object manager = getResidenceManager.invoke(residence);
            Method getByLoc = manager.getClass().getMethod("getByLoc", org.bukkit.Location.class);
            Object res = getByLoc.invoke(manager, block.getLocation());
            if (res == null) return true;
            Method isOwner = res.getClass().getMethod("isOwner", Player.class);
            Object ownerRes = isOwner.invoke(res, player);
            if (ownerRes instanceof Boolean && (Boolean) ownerRes) return true;

            Method getPermissions = res.getClass().getMethod("getPermissions");
            Object perms = getPermissions.invoke(res);
            Class<?> flagsCls = Class.forName("com.bekvon.bukkit.residence.containers.Flags");
            Object containerFlag = flagsCls.getField("container").get(null);
            Method playerHas = perms.getClass().getMethod("playerHas", Player.class, String.class, containerFlag.getClass(), boolean.class);
            Object has = playerHas.invoke(perms, player, player.getWorld().toString(), containerFlag, false);
            if (has instanceof Boolean && (Boolean) has) return true;

            Method isResAdminOn = residence.getClass().getMethod("isResAdminOn", Player.class);
            Object adminOn = isResAdminOn.invoke(residence, player);
            return adminOn instanceof Boolean && (Boolean) adminOn;
        } catch (Throwable ignored) {
            return true;
        }
    }

    @Override
    public Boolean protectBlock(Player player, Block block) {
        return false;
    }
}
