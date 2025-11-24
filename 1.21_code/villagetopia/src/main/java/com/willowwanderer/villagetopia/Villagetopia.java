package com.willowwanderer.villagetopia;

import com.willowwanderer.villagetopia.block.VillageStone;
import com.willowwanderer.villagetopia.entity.ModEntities;
import com.willowwanderer.villagetopia.item.EmeraldBits;
import com.willowwanderer.villagetopia.item.VillageStoneItem;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Villagetopia.MOD_ID)
public class Villagetopia
{
    public static final String MOD_ID = "villagetopia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public Villagetopia(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        // Add items
        EmeraldBits.register(modEventBus);
        VillageStone.register(modEventBus);
        VillageStoneItem.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON,Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Empty method
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        // Add items
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(EmeraldBits.EMERALD_BITS);
        }

        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){
            event.accept(VillageStone.CENTRAL_VILLAGE_STONE);
            event.accept(VillageStone.BOUNDARY_VILLAGE_STONE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Empty method
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Empty method
        }
    }
}
