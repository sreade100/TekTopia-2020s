package com.willowwanderer.villagetopia.entity.villager;

import java.util.List;

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

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

    private String job = "NONE";
    private float hunger = 0f;
    private float happiness = 0f;
    private String[] traits = new String[3];

    // Constructor
    public ModVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        this.job = "NONE"; // default
        this.hunger = 10.000f; // start at maximum
        this.happiness = 5.000f; // start in middle
        this.traits = this.generateRandomTraits(); // generates random traits
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
        tag.putString("Job", job);
        tag.putFloat("Happiness", happiness);
        tag.putFloat("Hunger", hunger);
        ListTag traitList = new ListTag();

        for (String trait : traits) {
            traitList.add(StringTag.valueOf(trait));
        }

        tag.put("Traits", traitList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.job = tag.getString("Job");
        this.happiness = tag.getFloat("Happiness");
        this.hunger = tag.getFloat("Hunger");

        if (tag.contains("Traits", Tag.TAG_LIST)) {

            ListTag traitList = tag.getList("Traits", Tag.TAG_STRING);

            this.traits = new String[traitList.size()];

            for (int i = 0; i < traitList.size(); i++) {
                this.traits[i] = traitList.getString(i);
            }

        } else {
            this.traits = generateRandomTraits();
        }
    }

    // -------------------------------
    // Getters / Setters
    // -------------------------------
    public String getJob() {
        return job;
    }

    public String getRandomJob(VillageData data){
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

    public void setJob(VillageData data) {
        setJob(getRandomJob(data));
    }

    public void setJob(String job) {
        this.job = job;
        setVillagerProfession(job);
    }

    public float getHappiness() {
        return happiness;
    }

    public void setHappiness(float happiness) {
        this.happiness = happiness;
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = hunger;
    }

    public String[] getTraits(){
        return traits;
    }

    public String[] generateRandomTraits() {

        String[] result = new String[3];

        List<TraitDefinition> available = TraitDatabase.getTraits();

        for (int i = 0; i < 3; i++) {

            double totalWeight = 0;

            for (TraitDefinition trait : available) {
                totalWeight += trait.getWeight();
            }

            double random = Math.random() * totalWeight;

            for (TraitDefinition trait : available) {

                random -= trait.getWeight();

                if (random <= 0) {
                    result[i] = trait.getName();
                    break;
                }
            }
        }

        return result;
    }

    // -------------------------------
    // VillagerEntity logic
    // -------------------------------


    // -------------------------------
    // Trader-like appearance
    // -------------------------------
    private void setVillagerProfession(String job) {
        // Pick a random basic profession
        VillagerProfession profession = getProfession(job); //pickRandomProfession();

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

    private VillagerProfession getProfession(String job) {
        switch (job.toUpperCase()) {
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

    private void tickHunger() {
        this.hunger = this.hunger - 0.000139f; // starve in three days after full

        if (this.hunger < 0.0f){
            if (this.getHealth() < 0.1f){
                String message = this.getName() + " died of starvation";
                broadcast(getServer(), message);
            }
            this.hurt(this.damageSources().starve(),1.0f);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        tickHunger();
    }

    public static void broadcast(MinecraftServer server, String message) {
        server.getPlayerList().broadcastSystemMessage(
                Component.literal(message),
                false
        );
    }
}
