package com.saosao.jellyfishkitchen.worldgen;

import com.saosao.jellyfishkitchen.registry.ModBlocks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.core.registries.Registries;

public class ModSurfaceRules {
    private static final ResourceKey<Biome> JELLYFISH_FIELDS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("jellyfishkitchen", "jellyfish_fields.json"));

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.RuleSource makeRules() {
        // 定义方块引用
        SurfaceRules.RuleSource algaeGrass = makeStateRule(ModBlocks.ALGAE_GRASS.get());
        SurfaceRules.RuleSource coralstone = makeStateRule(ModBlocks.CORALSTONE.get());

        // 规则逻辑：
        // 如果当前是水母田群系：
        // 1. 在最表面（ON_FLOOR）放置藻类草
        // 2. 在下面（UNDER_FLOOR）放置珊瑚石
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(JELLYFISH_FIELDS),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, algaeGrass),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, coralstone)
                        )
                )
        );
    }
}