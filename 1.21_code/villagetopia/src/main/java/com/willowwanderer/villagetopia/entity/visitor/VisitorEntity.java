package com.willowwanderer.villagetopia.entity.visitor;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.behavior.SleepInBed;

import com.mojang.serialization.Dynamic;
import com.willowwanderer.villagetopia.entity.visitor.goals.DespawnAtSunsetGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;


public class VisitorEntity extends Villager {

    private String purpose = "NONE";
    private float stayLikelihood = 0f;

    // Constructor
    public VisitorEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level,VillagerType.DESERT);
        this.purpose = "NONE"; // default
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<Villager> brain = this.brainProvider().makeBrain(dynamic);

        return brain;
    }

    @Override
    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new DespawnAtSunsetGoal(this));
        super.registerGoals();
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes()  // start with default villager attributes
                .add(Attributes.MAX_HEALTH, 20.0D)       // set max health
                .add(Attributes.MOVEMENT_SPEED, 0.5D);   // set movement speed
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
        setVisitorProfession(purpose);
    }

    public float getStayLikelihood() {
        return stayLikelihood;
    }

    public void setStayLikelihood(float stayLikelihood) {
        this.stayLikelihood = stayLikelihood;
    }

    // -------------------------------
    // VisitorEntity logic
    // -------------------------------
    /**
     * Returns true for normal visitors; false if purpose is "OneNight"
     */
    public boolean oneNight() {
        return "ONENIGHT".equals(this.purpose);
    }

    // -------------------------------
    // Trader-like appearance
    // -------------------------------
    private void setVisitorProfession(String purpose) {
        // Pick a random basic profession
        VillagerProfession profession = getProfession(purpose); //pickRandomProfession();

        // Directly create a new VillagerData (since withProfession() doesn’t exist)
        VillagerData newData = new VillagerData(
                this.getVillagerData().getType(), // keep same villager type
                profession,                        // set chosen profession
                2                                  // level 2 for slightly experienced look
        );
        this.setVillagerData(newData);
        
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            // serverLevel is your ServerLevel instance
            this.refreshBrain(serverLevel);
        }

        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 16); 
        }
        
    } 

    private VillagerProfession getProfession(String purpose) {
        switch (purpose.toUpperCase()) {
            case "TRADE":
                return VillagerProfession.FARMER;          // placeholder for trader type
            case "EXPLORE":
                return VillagerProfession.FLETCHER;        // placeholder for explorer type
            case "SOCIALISE":
                return VillagerProfession.SHEPHERD;        // placeholder for social type
            case "ONENIGHT":
                return VillagerProfession.LEATHERWORKER;   // placeholder for temporary visitor type
            case "THIEF":
                return VillagerProfession.TOOLSMITH;       // placeholder for stealth/rogue type
            case "MURDER":
                return VillagerProfession.WEAPONSMITH;     // placeholder for aggressive type
            default:
                return VillagerProfession.NITWIT;          // fallback/default
        }
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

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        if (this.oneNight()) return;

        long time = this.level().getDayTime() % 24000;
        if (time >= 12000) {
            this.discard();
        }
    }
}
