package com.willowwanderer.villagetopia.event;

import com.willowwanderer.villagetopia.Villagetopia;
import com.willowwanderer.villagetopia.block.VillageStone;
import com.willowwanderer.villagetopia.village.VillageData;
import com.willowwanderer.villagetopia.village.VillageStoneManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerLevel;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Villagetopia.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VillageEvents {

    // -----------------------------
    // Keep one manager per server-level
    // -----------------------------
    private static final Map<Level, VillageStoneManager> managers = new HashMap<>();

    private static VillageStoneManager getManager(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        return managers.computeIfAbsent(level, lvl -> {
            // Load or create persistent VillageData
            VillageData data = VillageData.getOrCreate(serverLevel);
            return new VillageStoneManager(data,serverLevel);
        });
    }

    // -----------------------------
    // Block placement
    // -----------------------------
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        Level level = event.getLevel() instanceof Level lvl ? lvl : null;
        if (level == null || level.isClientSide()) return;

        Block placed = event.getPlacedBlock().getBlock();
        if (placed == VillageStone.CENTRAL_VILLAGE_STONE.get()) {
            VillageStoneManager manager = getManager(level); //TODO: Add new manager if block placed too far from original block (i.e. create new manager)
            if (manager != null) {
                manager.addStone(event.getPos());
            }
        }
    }

    // -----------------------------
    // Block removal
    // -----------------------------
    @SubscribeEvent
    public static void onBlockRemoved(BlockEvent.BreakEvent event) {
        Level level = event.getLevel() instanceof Level lvl ? lvl : null;
        if (level == null || level.isClientSide()) return;

        Block broken = event.getState().getBlock();
        if (broken == VillageStone.CENTRAL_VILLAGE_STONE.get()) {
            VillageStoneManager manager = getManager(level);
            if (manager != null) {
                manager.removeStone(event.getPos());
            }
        }
    }

    // -----------------------------
    // Level tick
    // -----------------------------
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        VillageStoneManager manager = getManager(level);
        if (manager != null) {
            manager.tick(level);
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
    }
}
