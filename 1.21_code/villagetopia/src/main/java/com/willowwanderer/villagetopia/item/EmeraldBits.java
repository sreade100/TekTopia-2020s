package com.willowwanderer.villagetopia.item.emeraldbits;

import com.willowwanderer.villagetopia.Villagetopia;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EmeraldBits {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Villagetopia.MOD_ID);

    public static final DeferredItem<Item> EMERALD_BITS = ITEMS.register("emerald_bits",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
