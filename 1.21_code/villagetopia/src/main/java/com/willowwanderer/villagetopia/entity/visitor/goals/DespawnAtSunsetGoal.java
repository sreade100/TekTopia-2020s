package com.willowwanderer.villagetopia.entity.visitor.goals;

import com.willowwanderer.villagetopia.entity.visitor.VisitorEntity;

import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;


public class DespawnAtSunsetGoal extends Goal {

    private final VisitorEntity visitor;

    public DespawnAtSunsetGoal(VisitorEntity visitor) {
        this.visitor = visitor;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        // Only run on server, and only for one-night visitors
        if (visitor.level().isClientSide) return false;
        if (visitor.oneNight()) return false;

        long time = visitor.level().getDayTime() % 24000;
        // Active during sunset->night
        return time >= 12000;
    }

    @Override
    public void tick() {
        if (!visitor.isRemoved()) {
            visitor.discard();
        }
    }
}