package com.willowwanderer.villagetopia.block.villagestone;

import com.willowwanderer.villagetopia.Villagetopia;
import com.willowwanderer.villagetopia.item.villagestone.VillageStoneItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class VillageStone {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Villagetopia.MOD_ID);

    public static final DeferredBlock<Block> CENTRAL_VILLAGE_STONE = registerBlock("central_village_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .explosionResistance(10f).instabreak().sound(SoundType.STONE)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name,toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        VillageStoneItem.ITEMS.register(name,() -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}