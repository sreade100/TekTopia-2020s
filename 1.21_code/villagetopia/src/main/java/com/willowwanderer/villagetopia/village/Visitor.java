package com.willowwanderer.villagetopia.village;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public class Visitor {

    private final Villager villager;

    public Visitor(Villager villager) {
        this.villager = villager;
        markAsVisitor();
    }

    private void markAsVisitor() {
        // You can use custom tags to identify visitors
        villager.getPersistentData().putBoolean("villagetopia:visitor", true);
    }

    public boolean isVisitor() {
        return villager.getPersistentData().getBoolean("villagetopia:visitor");
    }

    public void tick(Level level) {
        // Despawn visitor at sunset
        long time = level.getDayTime() % 24000L;
        if (time > 12000L && isVisitor()) {
            villager.discard(); // Removes entity from the world
        }
    }

    /**
     * Updates the visitor each tick
     * @param dayTime current world time in ticks
     */
    public void update(long dayTime) {
        // Despawn at sunset (12000L = midday to sunset)
        long timeOfDay = dayTime % 24000L;
        if (timeOfDay > 12000L && isVisitor()) {
            villager.discard(); // removes from world
        }
    }

    public Villager getVillager() {
        return villager;
    }
}
