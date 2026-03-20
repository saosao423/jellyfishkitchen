package com.saosao.jellyfishkitchen.client;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.item.JellyfishNetItem;
import com.saosao.jellyfishkitchen.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端专属事件类
 * 负责注册物品模型谓词（用于切换水母网贴图）
 */
public class ClientModEvents {

    public static void onClientSetup(FMLClientSetupEvent event) {
        // 1.21 必须在 enqueueWork 中执行渲染属性注册，以确保线程安全
        event.enqueueWork(() -> {
            // 注册基础切换谓词 (空网/实网)
            // 对应 JSON 里的 "has_entity"
            ItemProperties.register(ModItems.JELLYFISH_NET.get(),
                    ResourceLocation.withDefaultNamespace("has_entity"),
                    (stack, level, entity, seed) -> JellyfishNetItem.getHasEntity(stack)
            );

            // 注册实体类型谓词 (牛/鸡/羊等的具体切换)
            // 对应 JSON 里的 "jellyfishkitchen:entity_type"
            ItemProperties.register(ModItems.JELLYFISH_NET.get(),
                    ResourceLocation.fromNamespaceAndPath(JellyfishKitchen.MODID, "entity_type"),
                    (stack, level, entity, seed) -> JellyfishNetItem.getEntityTypeForModel(stack)
            );
        });
    }
}