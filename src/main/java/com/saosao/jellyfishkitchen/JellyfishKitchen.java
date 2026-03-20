package com.saosao.jellyfishkitchen;

import com.mojang.logging.LogUtils;
import com.saosao.jellyfishkitchen.client.ClientModEvents;
import com.saosao.jellyfishkitchen.registry.ModBlocks;
import com.saosao.jellyfishkitchen.registry.ModItems;
import com.saosao.jellyfishkitchen.registry.ModTerrablender;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

/**
 * Mod 主入口类
 */
@Mod(JellyfishKitchen.MODID)
public class JellyfishKitchen {
    // 确保这里的 MODID 与你 resources/META-INF/neoforge.mods.toml 里的 modId 一致
    public static final String MODID = "jellyfishkitchen";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JellyfishKitchen(IEventBus modEventBus) {
        // 1. 注册核心组件
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        modEventBus.addListener(this::setupTerrablender);

        // 2. 注册加载阶段的通用设置
        modEventBus.addListener(this::commonSetup);

        // 3. 注册物品栏添加事件
        modEventBus.addListener(this::addCreative);

        // 4. 手动注册客户端设置事件（消除警告的关键）
        // 这行代码会告诉 NeoForge 去 ClientModEvents 类里运行 onClientSetup 方法
        modEventBus.addListener(ClientModEvents::onClientSetup);

        // 注意：不要在此调用 NeoForge.EVENT_BUS.register(this)，
        // 因为本类中目前没有任何带有 @SubscribeEvent 的方法。
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Jellyfish Kitchen 正在加载通用设置...");
    }

    private void setupTerrablender(FMLCommonSetupEvent event) {
        event.enqueueWork(ModTerrablender::register);
    }

    /**
     * 将物品添加到原版的创意模式栏
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 将水母网添加到工具栏
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.JELLYFISH_NET);
        }

        // 将方块添加到建筑方块栏
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.CORALSTONE);
            event.accept(ModItems.ALGAE_GRASS);
        }
    }
}