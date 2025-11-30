package org.wargamer2010.signshop.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class BlockLockerHook implements Hook {
    @Override
    public String getName() {
        return "BlockLocker";
    }

    @Override
    public Boolean canBuild(Player player, Block block) {
        if (HookManager.getHook("BlockLocker") == null)
            return true;
        try {
            Class<?> api = Class.forName("nl.rutgerkok.blocklocker.BlockLockerAPIv2");
            Method isAllowed = api.getMethod("isAllowed", Player.class, Block.class, boolean.class);
            Object result = isAllowed.invoke(null, player, block, true);
            return (result instanceof Boolean) ? (Boolean) result : true;
        } catch (Throwable ignored) {
            return true;
        }
    }

    @Override
    public Boolean protectBlock(Player player, Block block) {
        return false;
    }
}
