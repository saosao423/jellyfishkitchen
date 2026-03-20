package com.saosao.jellyfishkitchen.client;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.item.JellyfishNetItem;
import com.saosao.jellyfishkitchen.registry.ModItems;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = JellyfishKitchen.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // 建议放在 enqueueWork 中执行以保证线程安全
            ItemProperties.register(ModItems.JELLYFISH_NET.get(), 
                ResourceLocation.withDefaultNamespace("has_entity"), // 变更为 minecraft:has_entity
                (itemStack, level, entity, seed) -> JellyfishNetItem.getHasEntity(itemStack)
            );
            
            // 注册实体类型属性，用于模型切换
            ItemProperties.register(ModItems.JELLYFISH_NET.get(), 
                ResourceLocation.fromNamespaceAndPath(JellyfishKitchen.MODID, "entity_type"),
                (itemStack, level, entity, seed) -> JellyfishNetItem.getEntityTypeForModel(itemStack)
            );
        });
    }
}
