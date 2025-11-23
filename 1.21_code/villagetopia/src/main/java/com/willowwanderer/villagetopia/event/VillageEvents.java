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

        // Spawn a villager above the block
        Villager villager = EntityType.VILLAGER.create(level);
        if (villager != null) {
            villager.moveTo(
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    level.random.nextFloat() * 360F,
                    0
            );

            level.addFreshEntity(villager);
        }
    }
}
