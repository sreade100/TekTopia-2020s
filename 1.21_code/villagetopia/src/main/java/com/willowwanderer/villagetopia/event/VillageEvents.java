package com.willowwanderer.villagetopia.event;

import com.willowwanderer.villagetopia.Villagetopia;
import com.willowwanderer.villagetopia.block.VillageStone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Villagetopia.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VillageEvents {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {

        // Convert LevelAccessor → Level safely
        Level level = event.getLevel() instanceof Level lvl ? lvl : null;
        if (level == null || level.isClientSide()) return;

        // Get the block that was placed
        Block placedBlock = event.getPlacedBlock().getBlock();

        // Check if it matches your central village stone block
        if (placedBlock != VillageStone.CENTRAL_VILLAGE_STONE.get()) return;

        BlockPos pos = event.getPos();

        // Spawn a villager at a random position around the stone (radius 3 blocks)
        int radius = 3;
        double dx = pos.getX() + (Math.random() * (radius * 2 + 1) - radius) + 0.5;
        double dz = pos.getZ() + (Math.random() * (radius * 2 + 1) - radius) + 0.5;
        int dy = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) dx, (int) dz);

        Villager villager = EntityType.VILLAGER.create(level);
        if (villager != null) {
            villager.moveTo(dx, dy + 1, dz, level.random.nextFloat() * 360F, 0);
            level.addFreshEntity(villager);
        }
    }

    private static int tickCounter = 0; // optional counter to reduce log spam

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        tickCounter++;
        if (tickCounter >= 20) { // 20 ticks = ~1 second
            System.out.println("Minecraft time: " + (level.getDayTime() % 24000L));
            tickCounter = 0;
        }
    }
}
