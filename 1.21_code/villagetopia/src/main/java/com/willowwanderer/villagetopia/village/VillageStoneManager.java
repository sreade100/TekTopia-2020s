package com.willowwanderer.villagetopia.village;

import com.willowwanderer.villagetopia.village.Visitor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VillageStoneManager {

    private final List<BlockPos> villageStones = new ArrayList<>();
    private final Random random = new Random();

    // Keep a list of visitors
    private final List<Visitor> visitors = new ArrayList<>();
    
    public void addStone(BlockPos pos) {
        if (!villageStones.contains(pos)) {
            villageStones.add(pos);
        }
    }

    public void removeStone(BlockPos pos) {
        villageStones.remove(pos);
    }

    public void tick(Level level) {

        for (Visitor visitor : visitors){
            visitor.update(level.getDayTime());
        }

        if (level.isClientSide() || villageStones.isEmpty() || (level.getDayTime() % 24000L) > 12000L) return;

        float spawnChance = 0.001f; // per stone per tick
        int radius = 3;

        for (BlockPos stonePos : villageStones) {
            if (random.nextFloat() < spawnChance) {
                spawnVillager(level, stonePos, radius);
            }
        }
    }

    private void spawnVillager(Level level, BlockPos stonePos, int radius) {
        Villager villager = EntityType.VILLAGER.create(level);
        if (villager == null) return;

        double dx = stonePos.getX() + (random.nextDouble() * (radius * 2 + 1) - radius) + 0.5;
        double dz = stonePos.getZ() + (random.nextDouble() * (radius * 2 + 1) - radius) + 0.5;
        int dy = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) dx, (int) dz);

        villager.moveTo(dx, dy + 1, dz, level.random.nextFloat() * 360F, 0);
        level.addFreshEntity(villager);

        Visitor visitor = new Visitor(villager);
        visitors.add(visitor);
    }
}
