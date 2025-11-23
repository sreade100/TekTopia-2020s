package com.willowwanderer.villagetopia.village;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillageData extends SavedData {

    private final List<BlockPos> villageStones = new ArrayList<>();

    public VillageData() {
        super();
    }

    // -----------------------------
    // Getters
    // -----------------------------
    public List<BlockPos> getVillageStones() {
        return villageStones;
    }


    // -----------------------------
    // Mark data dirty for saving
    // -----------------------------
    public void markDirty() {
        setDirty();
    }

    // -----------------------------
    // Save / Load NBT
    // -----------------------------
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag stoneList = new ListTag();
        for (BlockPos pos : villageStones) {
            stoneList.add(LongTag.valueOf(pos.asLong()));
        }
        tag.put("VillageStones", stoneList);

        return tag;
    }

    public static VillageData load(CompoundTag tag, HolderLookup.Provider provider) {
        VillageData data = new VillageData();

        ListTag stoneList = tag.getList("VillageStones", 4);
        for (int i = 0; i < stoneList.size(); i++) {
            long posLong = ((LongTag) stoneList.get(i)).getAsLong();
            data.villageStones.add(BlockPos.of(posLong));
        }
        return data;
    }

    // -----------------------------
    // Get or create VillageData for a server level
    // -----------------------------
    public static VillageData getOrCreate(ServerLevel level) {
        SavedData.Factory<VillageData> factory =
                new SavedData.Factory<>(
                        VillageData::new,    // create new empty
                        VillageData::load   // load from NBT
                );

        return level.getDataStorage().computeIfAbsent(factory, "villagetopia_villages");
    }

    public void printData(String prefix) {
        System.out.println(prefix + " VillageData dump:");
        System.out.println("  Village stones: " + villageStones.size());
        for (BlockPos pos : villageStones) {
            System.out.println("    Stone at: " + pos);
        }
    }

}
