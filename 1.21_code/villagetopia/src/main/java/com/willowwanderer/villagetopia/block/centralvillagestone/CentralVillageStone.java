package com.willowwanderer.villagetopia.block.centralvillagestone;

import com.mojang.serialization.MapCodec; // Used for serializing/deserializing block properties
import net.minecraft.core.BlockPos; // Represents a specific position in the game world
import net.minecraft.network.chat.Component; // Handles translatable messages like GUI titles
import net.minecraft.stats.Stats; // Tracks various player statistics
import net.minecraft.world.InteractionResult; // Enum for outcomes of block interactions
import net.minecraft.world.MenuProvider; // Interface for objects that provide a menu (GUI)
import net.minecraft.world.SimpleMenuProvider; // Simplified implementation of MenuProvider
import net.minecraft.world.entity.player.Player; // Represents a player entity in the game
import net.minecraft.world.inventory.ContainerLevelAccess; // Provides access to the block's position and level for containers
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level; // Represents the game world
import net.minecraft.world.level.block.CraftingTableBlock; // Parent class for crafting table-like blocks
import net.minecraft.world.level.block.state.BlockBehaviour; // Used to define properties of a block (e.g., material, hardness)
import net.minecraft.world.level.block.state.BlockState; // Represents the current state/configuration of a block
import net.minecraft.world.phys.BlockHitResult; // Contains information about a ray-traced block hit
import org.jetbrains.annotations.NotNull; // Annotation to indicate that a parameter or return value cannot be null

/**
 * The CentralVillageStone class represents a custom block in the game.
 * This block behaves like a crafting table.
 * It inherits behavior and properties from the CraftingTableBlock class.
 */
public class CentralVillageStone extends CraftingTableBlock {
    // A codec used for serializing and deserializing the block's properties. So it can be saved
    public static final MapCodec<CentralVillageStone> CODEC = simpleCodec(CentralVillageStone::new);

    // The title text displayed at the top of the GUI when interacting with this block
    private static final Component CONTAINER_TITLE = Component.translatable("container.villagetopia");

    /**
     * Returns the codec associated with this block.
     * This is used by Minecraft to handle serialization of block properties.
     *
     * @return MapCodec for CentralVillageStone, enabling its registration in the game.
     */
    @Override
    public @NotNull MapCodec<CentralVillageStone> codec() {
        return CODEC;
    }

    /**
     * Constructor for CentralVillageStone.
     * Passes block properties (like material and hardness) to the parent class constructor.
     *
     * @param properties Defines how the block behaves, e.g., its hardness, sound, and material type.
     */
    public CentralVillageStone(BlockBehaviour.Properties properties) {
        super(properties); // Pass properties to the CraftingTableBlock parent class
    }

    /**
     * Provides the menu (GUI) associated with this block.
     * This method is called when a player interacts with the block to open its GUI.
     *
     * @param state The current state of the block (e.g., facing direction, active/inactive).
     * @param level The game level in which the block resides.
     * @param pos   The position of the block in the world.
     * @return A SimpleMenuProvider that creates and manages the SmithingMenu GUI.
     */
    @Override
    public @NotNull MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        // Creates a SimpleMenuProvider with a CraftingMenu and a GUI title
        return new SimpleMenuProvider(
                // The first argument is a lambda that creates the CraftingMenu object.
                // - `p_277304_`: The ID of the container being opened.
                // - `p_277305_`: The inventory of the player opening the container.
                // - `p_277306_`: Access to the block's position and level.
                (p_277304_, p_277305_, p_277306_) -> new CraftingMenu(p_277304_, p_277305_, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE // Sets the title of the GUI as "container.villagetopia"
        );
    }

    /**
     * Handles the interaction logic when a player right-clicks on the block without holding an item.
     *
     * @param state     The current state of the block.
     * @param level     The game level in which the block is located.
     * @param pos       The position of the block being interacted with.
     * @param player    The player interacting with the block.
     * @param hitResult Contains information about the block hit (e.g., which side was clicked).
     * @return The result of the interaction:
     *         - SUCCESS: Indicates a valid interaction on the client-side.
     *         - CONSUME: Indicates that the interaction was handled on the server-side.
     */
    @Override
    public @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult
    ) {
        // Client-side logic: Return SUCCESS to indicate the interaction was valid
        if (level.isClientSide) {
            System.out.println("InteractionResult.SUCCESS");
            return InteractionResult.SUCCESS;
        } else {
            // Server-side logic:
            // Open the block's menu (Crafting Table GUI) for the player
            player.openMenu(state.getMenuProvider(level, pos));

            // Award the player a statistic for interacting with this type of block
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);

            // Return CONSUME to indicate the interaction was processed successfully
            System.out.println("InteractionResult.CONSUME_PARTIAL");
            return InteractionResult.CONSUME_PARTIAL;
        }
    }
}
