package com.saosao.jellyfishkitchen.registry;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.blocks.AlgaeGrassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册类
 * 优化点：
 * 1. 使用 ofFullCopy(Blocks.STONE) 确保物理属性（硬度、抗性、挖掘等级）与原版石头完全一致。
 * 2. 为 ALGAE_GRASS 开启了 randomTicks()，确保其生存逻辑正常运行。
 */
public class ModBlocks {

    // 创建方块注册表，关联 ModID
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JellyfishKitchen.MODID);

    /**
     * 珊瑚石 (Coralstone)
     * 物理属性：完全克隆原版石头 (1.5 硬度, 6.0 抗性)
     * 修改点：将地图颜色设为紫色
     */
    public static final DeferredBlock<Block> CORALSTONE = BLOCKS.register("coralstone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .mapColor(MapColor.COLOR_PURPLE)
            ));

    /**
     * 藻类草方块 (Algae Grass)
     * 物理属性：克隆原版石头
     * 修改点：
     * - 地图颜色设为植物绿
     * - 声音设为湿草声 (WET_GRASS)
     * - 开启随机刻 (randomTicks)，允许执行 AlgaeGrassBlock 中的 randomTick 逻辑
     */
    public static final DeferredBlock<Block> ALGAE_GRASS = BLOCKS.register("algae_grass",
            () -> new AlgaeGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .mapColor(MapColor.PLANT)
                    .sound(SoundType.WET_GRASS)
                    .randomTicks()
            ));

    /**
     * 将注册表连接到模组总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}