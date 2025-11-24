package com.willowwanderer.villagetopia.entity;

import java.util.function.Supplier;

import com.willowwanderer.villagetopia.Villagetopia;
import com.willowwanderer.villagetopia.entity.visitor.VisitorEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Villagetopia.MOD_ID);

    public static final Supplier<EntityType<VisitorEntity>> VISITOR = ENTITY_TYPES.register(
    "visitor",
    () -> EntityType.Builder.of(VisitorEntity::new, MobCategory.CREATURE)
            .sized(0.6f, 1.95f)
            .build("visitor")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
    
}