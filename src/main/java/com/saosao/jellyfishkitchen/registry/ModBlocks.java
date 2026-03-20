package com.saosao.jellyfishkitchen.registry;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.blocks.AlgaeGrassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JellyfishKitchen.MODID);

    // 珊瑚石
    public static final DeferredBlock<Block> CORALSTONE = BLOCKS.register("coralstone",
        () -> new Block(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(1.5F, 6.0F) // 修改为 1.5 和 6.0，与原版石头对齐
                .sound(SoundType.STONE)
                // 关键点：这一行意味着如果不使用正确的工具（镐），就不掉落东西
                .requiresCorrectToolForDrops())); 

    // 藻类草方块
    public static final DeferredBlock<Block> ALGAE_GRASS = BLOCKS.register("algae_grass",
        () -> new AlgaeGrassBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(1.5F, 6.0F) // 也要设为 1.5/6.0
                .sound(SoundType.WET_GRASS)
                .requiresCorrectToolForDrops()));

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}