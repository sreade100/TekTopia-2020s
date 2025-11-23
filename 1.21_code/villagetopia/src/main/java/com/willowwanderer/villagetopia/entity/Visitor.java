package com.willowwanderer.villagetopia.entity;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class Visitor extends Villager {

    private String purpose = "NONE";
    private float stayLikelihood = 0f;

    // Constructor
    public Visitor(EntityType<? extends Villager> entityType, Level level,String purpose) {
        super(entityType, level);
        this.purpose = purpose;
        setVisitorAppearance();
    }

    // -------------------------------
    // Save / Load custom visitor metadata
    // -------------------------------
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Purpose", purpose);
        tag.putFloat("StayLikelihood", stayLikelihood);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.purpose = tag.getString("Purpose");
        this.stayLikelihood = tag.getFloat("StayLikelihood");
    }

    // -------------------------------
    // Getters / Setters
    // -------------------------------
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public float getStayLikelihood() {
        return stayLikelihood;
    }

    public void setStayLikelihood(float stayLikelihood) {
        this.stayLikelihood = stayLikelihood;
    }

    // -------------------------------
    // Visitor logic
    // -------------------------------
    /**
     * Returns true for normal visitors; false if purpose is "OneNight"
     */
    public boolean oneNight() {
        return !"ONENIGHT".equals(this.purpose);
    }

    // -------------------------------
    // Trader-like appearance
    // -------------------------------
    private void setVisitorAppearance() {
        // Pick a random basic profession
        VillagerProfession profession = pickRandomProfession();

        // Directly create a new VillagerData (since withProfession() doesn’t exist)
        VillagerData newData = new VillagerData(
                this.getVillagerData().getType(), // keep same villager type
                profession,                        // set chosen profession
                2                                  // level 2 for slightly experienced look
        );
        this.setVillagerData(newData);
    } 

    private VillagerProfession pickRandomProfession() {
        VillagerProfession[] professions = new VillagerProfession[] {
            VillagerProfession.FARMER,
            VillagerProfession.FISHERMAN,
            VillagerProfession.SHEPHERD,
            VillagerProfession.FLETCHER,
            VillagerProfession.TOOLSMITH,
            VillagerProfession.WEAPONSMITH,
            VillagerProfession.LEATHERWORKER
        };
        int index = this.random.nextInt(professions.length);
        return professions[index];
    }
}
