package com.saosao.jellyfishkitchen.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class JellyfishFieldsRegion extends Region {
    public JellyfishFieldsRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // 定义我们的群系 Key
        ResourceKey<Biome> JELLYFISH_FIELDS = ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath("jellyfishkitchen", "jellyfish_fields.json"));

        // 简单的放置策略：在类似“平原”或“海洋”气候的地方生成
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            // 这里将水母田随机替换掉一些原本会出现平原的地方
            builder.replaceBiome(Biomes.PLAINS, JELLYFISH_FIELDS);
        });
    }
}