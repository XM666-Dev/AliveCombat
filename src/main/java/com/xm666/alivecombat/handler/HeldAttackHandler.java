package com.xm666.alivecombat.handler;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingShield(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        var item = living.getUseItem();
        var tags = item.getTags();
        return tags.map(TagKey::location).map(Identifier::toString).anyMatch(s -> s.equals("c:tools/shield"));
    }
}
