package com.willowwanderer.villagetopia.item.villagestone;

import com.willowwanderer.villagetopia.Villagetopia;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VillageStoneItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Villagetopia.MOD_ID);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}