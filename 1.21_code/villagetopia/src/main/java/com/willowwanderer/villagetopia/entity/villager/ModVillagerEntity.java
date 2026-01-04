package com.willowwanderer.villagetopia.entity.villager;

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
import com.willowwanderer.villagetopia.village.VillageData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Villager Behaviour:
 * Schedule (Non-guard Adult):
 * 00010 Talk & Eat
 * -> 02000 Work
 * -> 09000 Talk & Eat
 * -> 12000 Sleep
 * 
 * Schedule (Guard Adult):
 * 00010 Talk & Eat
 * -> 02000 Work
 * -> 09000 Talk & Eat
 * -> 12000 Sleep
 * 
 * Schedule (Non-school Child)
 * 00010 Talk & Eat
 * -> 02000 Work
 * -> 06000 Play
 * -> 09000 Talk & Eat
 * -> 10000 Play
 * -> 11000 Sleep
 * 
 * Schedule (School Child)
 * 00010 Talk & Eat
 * -> 02000 School
 * -> 09000 Talk & Eat
 * -> 10000 Play
 * -> 11000 Sleep
 * 
 * 
 * 
 * Automatic priority order of Jobs:
 * Farmer: Fence with sign of hoe (bool) && No spare food
 * 
 * Custom job setting
 * 
 * Happiness gained from:
 * Socialising with happy +
 * Socialising with sad -
 * Woohoo ++
 * Music ++
 * Good food +
 * Very good food ++
 * Get married +++
 * Have kid +++
 * Non-guard see murder --- 
 * Non-guard see thief -
 * Non-guard see mob --
 * See murder ---
 * Dead partner ---
 * Dead child ---
 * 
 */

public class ModVillagerEntity extends Villager {

    private String purpose = "NONE";
    private float stayLikelihood = 0f;

    // Constructor
    public ModVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
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

    public String getRandomPurpose(VillageData data){
        int total =
        data.trade +
        data.explore +
        data.socialise +
        data.thief +
        data.murder;

        // Safety fallback
        if (total <= 0) {
            return "TRADE";
        }

        int roll = this.random.nextInt(total);

        if ((roll -= data.trade) < 0) {
            return "TRADE";
        }
        if ((roll -= data.explore) < 0) {
            return "EXPLORE";
        }
        if ((roll -= data.socialise) < 0) {
            return "SOCIALISE";
        }
        if ((roll -= data.thief) < 0) {
            return "THIEF";
        }

        return "MURDER";
    }

    public void setPurpose(VillageData data) {
        setPurpose(getRandomPurpose(data));
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
        return "ONENIGHT".equals(this.purpose.toUpperCase());
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
