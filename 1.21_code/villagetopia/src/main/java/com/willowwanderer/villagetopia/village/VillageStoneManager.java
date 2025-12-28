package com.willowwanderer.villagetopia.village;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.willowwanderer.villagetopia.entity.ModEntities;
import com.willowwanderer.villagetopia.entity.visitor.VisitorEntity;


public class VillageStoneManager {

    private final VillageData data; // persistent data
    private final Random random = new Random();
    private float spawnChance = 0.0001f;
    private int radius = 20;


    public VillageStoneManager(VillageData data, ServerLevel level) {
        this.data = data;
    }

    // -----------------------------
    // Stone management
    // -----------------------------
    public void addStone(BlockPos pos) {
        if (!data.getVillageStones().contains(pos)) {
            data.getVillageStones().add(pos);
            data.markDirty();
        }
    }

    public void removeStone(BlockPos pos) {
        if (data.getVillageStones().remove(pos)) {
            data.markDirty();
        }
    }

    // -----------------------------
    // Tick method
    // -----------------------------
    public void tick(Level level) {

        if (level.isClientSide() || data.getVillageStones().isEmpty() ||
                (level.getDayTime() % 24000L) > 12000L) return;

        // Spawn villagers near each stone
        for (BlockPos stonePos : data.getVillageStones()) {
            if (random.nextFloat() < spawnChance) {
                spawnVisitor(level, stonePos, radius);
            }
        }
    }

    // -----------------------------
    // Spawn a vistor visitor near a stone
    // -----------------------------
    private void spawnVisitor(Level level, BlockPos stonePos, int radius) {

        for (int attempts = 0; attempts < 10; attempts++) {

            double dx = stonePos.getX() + (random.nextDouble() * (radius * 2 + 1) - radius) + 0.5;
            double dz = stonePos.getZ() + (random.nextDouble() * (radius * 2 + 1) - radius) + 0.5;

            int dy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) dx, (int) dz);
            BlockPos spawnPos = new BlockPos((int) dx, dy, (int) dz);

            // Check the block under the spawn position
            BlockPos groundPos = spawnPos.below();
            BlockState groundState = level.getBlockState(groundPos);

            boolean validGround =
                    groundState.is(Blocks.GRASS_BLOCK) ||
                    groundState.is(Blocks.SAND) ||
                    groundState.is(Blocks.RED_SAND);

            if (!validGround)
                continue; // try another location

            // Create the visitor from your entity registry
            VisitorEntity visitor = new VisitorEntity(EntityType.VILLAGER, level);
            if (visitor == null)
                return;

            visitor.moveTo(dx, dy + 1, dz, level.random.nextFloat() * 360F, 0);
            level.addFreshEntity(visitor);

            // Assign a purpose
            visitor.setPurpose(data);

            // Assign stay likelihood
            Random random = new Random();
            float stayLikelihood;

            if (random.nextFloat() < 0.1f) {
                stayLikelihood = 0.5f + random.nextFloat() * 0.5f;  // 0.5–1.0
            } else {
                stayLikelihood = random.nextFloat() * 0.2f;        // 0.0–0.2
            }

            visitor.setStayLikelihood(stayLikelihood);

            // Successfully spawned
            return;
        }

        // No valid spawn after 10 attempts – silently fail
    }


}
