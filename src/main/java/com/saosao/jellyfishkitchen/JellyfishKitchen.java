package com.saosao.jellyfishkitchen;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import com.saosao.jellyfishkitchen.registry.ModItems;
import com.saosao.jellyfishkitchen.registry.ModBiomes;
import com.saosao.jellyfishkitchen.registry.ModBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.resources.ResourceLocation;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JellyfishKitchen.MODID)
public class JellyfishKitchen {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "jellyfishkitchen";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public JellyfishKitchen(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register mod items
        ModItems.register(modEventBus);
        // Register mod blocks
        ModBlocks.register(modEventBus);
        // Register mod biomes
        // ModBiomes.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Common setup code
    }

    // Add items and blocks to creative tabs
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.JELLYFISH_NET);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.CORALSTONE);
            event.accept(ModBlocks.ALGAE_GRASS);
        }
    }



    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
