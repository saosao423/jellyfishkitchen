package com.saosao.jellyfishkitchen.item;

import com.saosao.jellyfishkitchen.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class JellyfishNetItem extends Item {

    public JellyfishNetItem(Properties properties) {
        super(properties);
    }

    // --- 数据组件助手方法 ---
    public static boolean hasEntity(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.contains("id");
    }

    public static void storeEntityData(ItemStack stack, CompoundTag entityData) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(entityData));
    }

    public static CompoundTag getEntityData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : null;
    }

    // --- 核心释放逻辑（复用） ---
    private InteractionResult releaseEntity(Player player, Level level, ItemStack stack, InteractionHand hand, Vec3 spawnPos) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        CompoundTag entityData = getEntityData(stack);
        if (entityData == null) return InteractionResult.PASS;

        return EntityType.byString(entityData.getString("id")).map(type -> {
            Entity entity = type.create(serverLevel);
            if (entity != null) {
                entity.load(entityData);
                entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0);

                if (serverLevel.addFreshEntity(entity)) {
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos);

                    // 转换并消耗耐久
                    ItemStack result = new ItemStack(ModItems.JELLYFISH_NET.get());
                    result.setDamageValue(stack.getDamageValue());
                    result.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                    player.setItemInHand(hand, result);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!hasEntity(context.getItemInHand())) return InteractionResult.PASS;
        return releaseEntity(context.getPlayer(), context.getLevel(), context.getItemInHand(), context.getHand(), context.getClickLocation());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hasEntity(stack)) {
            Vec3 spawnPos = player.position().add(player.getLookAngle().scale(1.5));
            InteractionResult result = releaseEntity(player, level, stack, hand, spawnPos);
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hasEntity(stack) || player.getCooldowns().isOnCooldown(this)) return InteractionResult.PASS;

        // 允许捕捉的目标
        if (target instanceof Cow || target instanceof Chicken || target instanceof Sheep || target instanceof Pig || target instanceof Wolf || target instanceof Fox) {
            player.swing(hand);
            if (!player.level().isClientSide() && target.isAlive()) {
                player.getCooldowns().addCooldown(this, 20);
                player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.NEUTRAL, 1.0F, 1.0F);

                // 核心：保存数据并补全 ID
                CompoundTag entityData = new CompoundTag();
                target.save(entityData);
                entityData.putString("id", EntityType.getKey(target.getType()).toString());

                ItemStack caughtStack = stack.copy(); // 保持当前耐久
                storeEntityData(caughtStack, entityData);
                caughtStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                target.discard();
                player.setItemInHand(hand, caughtStack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // --- 客户端模型谓词方法 ---
    public static float getHasEntity(ItemStack stack) { return hasEntity(stack) ? 1.0F : 0.0F; }

    public static float getEntityTypeForModel(ItemStack stack) {
        if (!hasEntity(stack)) return 0.0F;
        String id = getEntityData(stack).getString("id");
        return switch (id) {
            case "minecraft:cow" -> 1.0F;
            case "minecraft:chicken" -> 2.0F;
            case "minecraft:sheep" -> 3.0F;
            case "minecraft:pig" -> 4.0F;
            case "minecraft:wolf" -> 5.0F;
            case "minecraft:fox" -> 6.0F;
            default -> 0.0F;
        };
    }
}