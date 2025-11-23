package com.willowwanderer.villagetopia.event;

import com.willowwanderer.villagetopia.Villagetopia;
import com.willowwanderer.villagetopia.block.VillageStone;
import com.willowwanderer.villagetopia.village.VillageStoneManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Villagetopia.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VillageEvents {

    // Keep a manager per level
    private static final Map<Level, VillageStoneManager> managers = new HashMap<>();

    private static VillageStoneManager getManager(Level level) {
        return managers.computeIfAbsent(level, lvl -> new VillageStoneManager());
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        Level level = event.getLevel() instanceof Level lvl ? lvl : null;
        if (level == null || level.isClientSide()) return;

        Block placed = event.getPlacedBlock().getBlock();
        if (placed == VillageStone.CENTRAL_VILLAGE_STONE.get()) {
            getManager(level).addStone(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBlockRemoved(BlockEvent.BreakEvent event) {
        Level level = event.getLevel() instanceof Level lvl ? lvl : null;
        if (level == null || level.isClientSide()) return;

        Block broken = event.getState().getBlock();
        if (broken == VillageStone.CENTRAL_VILLAGE_STONE.get()) {
            getManager(level).removeStone(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        getManager(level).tick(level); // Trigger per-world spawn logic
    }
}
