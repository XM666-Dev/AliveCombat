package com.xm666.alivecombat.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.Tags;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingShield(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        var item = living.getUseItem();
        return item.is(Tags.Items.TOOLS_SHIELD);
    }
}
