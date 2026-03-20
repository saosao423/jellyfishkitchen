package com.saosao.jellyfishkitchen.registry;

import com.saosao.jellyfishkitchen.JellyfishKitchen;
import com.saosao.jellyfishkitchen.item.JellyfishNetItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JellyfishKitchen.MODID);

    // 水母网 - 耐久度148
    public static final DeferredItem<Item> JELLYFISH_NET = ITEMS.register("jellyfish_net",
            () -> new JellyfishNetItem(new Item.Properties().durability(148)));

    // 珊瑚石物品
    public static final DeferredItem<Item> CORALSTONE = ITEMS.register("coralstone",
            () -> new BlockItem(ModBlocks.CORALSTONE.get(), new Item.Properties()));

    // 藻类草方块物品
    public static final DeferredItem<Item> ALGAE_GRASS = ITEMS.register("algae_grass",
            () -> new BlockItem(ModBlocks.ALGAE_GRASS.get(), new Item.Properties()));

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
