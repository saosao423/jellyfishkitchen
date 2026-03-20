package com.saosao.jellyfishkitchen.registry;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.worldgen.ModSurfaceRules;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class ModTerrablender {
    public static void register() {
        // 注册区域：告诉游戏我们的群系出现在哪。参数：RegionID, 权重（越高越常见）
        Regions.register(new JellyfishFieldsRegion(ResourceLocation.fromNamespaceAndPath(JellyfishKitchen.MODID, "overworld_region"), 5));

        // 注册地表规则：告诉游戏在水母田群系里，表面用什么方块，下面用什么方块
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, JellyfishKitchen.MODID, ModSurfaceRules.makeRules());
    }
}