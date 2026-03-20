package com.saosao.jellyfishkitchen.item;

import com.saosao.jellyfishkitchen.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class JellyfishNetItem extends Item {

    public JellyfishNetItem(Properties properties) {
        super(properties);
    }

    // 检查是否有实体
    public static boolean hasEntity(ItemStack stack) {
    CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
    if (customData == null) return false;
    // 检查标签内是否有 id 字段（因为你存储实体时肯定会有 "id"）
    return customData.contains("id");
    }

    // 存储实体数据
    public static void storeEntityData(ItemStack stack, CompoundTag entityData) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(entityData));
    }

    // 清除实体数据
    public static void clearEntityData(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
    }

    // 获取实体数据
    public static CompoundTag getEntityData(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : null;
    }

    // 右键点击方块
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        if (!hasEntity(stack)) {
            return super.useOn(context);
        }

        CompoundTag entityData = getEntityData(stack);
        if (entityData == null) {
            return InteractionResult.PASS;
        }

        // 生成实体
        EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
        if (entityType != null) {
            Entity entity = entityType.create(serverLevel);
            if (entity != null) {
                entity.load(entityData);
                entity.setPos(context.getClickLocation());
                entity.setDeltaMovement(0, 0, 0);
                
                if (serverLevel.addFreshEntity(entity)) {
                    // 播放音效
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.BUCKET_EMPTY_FISH, entity.getSoundSource(), 1.0F, 1.0F);

                    // 恢复为普通水母网
                    ItemStack newNet = new ItemStack(ModItems.JELLYFISH_NET.get());
                    newNet.setDamageValue(stack.getDamageValue());

                    // 消耗耐久度
                    if (newNet.getDamageValue() + 1 < newNet.getMaxDamage()) {
                        newNet.setDamageValue(newNet.getDamageValue() + 1);
                    } else {
                        // 耐久度耗尽
                        newNet.setCount(0);
                    }

                    // 替换物品
                    context.getPlayer().setItemInHand(context.getHand(), newNet);
                    context.getPlayer().awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, entity.position());

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    // 右键点击空气
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasEntity(stack)) {
            return super.use(level, player, hand);
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        CompoundTag entityData = getEntityData(stack);
        if (entityData == null) {
            return InteractionResultHolder.pass(stack);
        }

        // 生成实体
        EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
        if (entityType != null) {
            Entity entity = entityType.create(serverLevel);
            if (entity != null) {
                entity.load(entityData);
                entity.setPos(player.getX(), player.getY() + 1, player.getZ());
                entity.setDeltaMovement(0, 0, 0);
                
                if (serverLevel.addFreshEntity(entity)) {
                    // 播放音效
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.BUCKET_EMPTY_FISH, entity.getSoundSource(), 1.0F, 1.0F);

                    // 恢复为普通水母网
                    ItemStack newNet = new ItemStack(ModItems.JELLYFISH_NET.get());
                    newNet.setDamageValue(stack.getDamageValue());

                    // 消耗耐久度
                    if (newNet.getDamageValue() + 1 < newNet.getMaxDamage()) {
                        newNet.setDamageValue(newNet.getDamageValue() + 1);
                    } else {
                        // 耐久度耗尽
                        newNet.setCount(0);
                    }

                    // 替换物品
                    player.setItemInHand(hand, newNet);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());

                    return InteractionResultHolder.success(newNet);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    // 右键点击实体（捕捉功能）
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // 检查水母网是否已经有实体
        if (hasEntity(stack)) {
            return InteractionResult.PASS;
        }

        // 检查冷却时间
        if (player.getCooldowns().isOnCooldown(ModItems.JELLYFISH_NET.get())) {
            return InteractionResult.PASS;
        }

        // 检查是否是我们要捕捉的动物类型
        if (target instanceof Cow || target instanceof Chicken || target instanceof Sheep || target instanceof Pig || target instanceof Wolf || target instanceof Fox) {
            // 播放手臂挥动动画
            player.swing(hand);

            // 只在服务端执行
            if (!player.level().isClientSide() && target.isAlive()) {
                // 设置冷却时间（1秒 = 20 ticks）
                player.getCooldowns().addCooldown(ModItems.JELLYFISH_NET.get(), 20);

                // 播放捕捉音效
                player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ARMOR_EQUIP_CHAIN, target.getSoundSource(), 1.0F, 1.0F);

                // 保存实体的NBT数据
                CompoundTag entityData = new CompoundTag();
                target.save(entityData);

                // 创建新的水母网，存储实体数据
                ItemStack newNet = new ItemStack(ModItems.JELLYFISH_NET.get());
                newNet.setDamageValue(stack.getDamageValue());
                storeEntityData(newNet, entityData);

                // 消耗耐久度
                if (newNet.getDamageValue() + 1 < newNet.getMaxDamage()) {
                    newNet.setDamageValue(newNet.getDamageValue() + 1);
                } else {
                    // 耐久度耗尽
                    newNet.setCount(0);
                }

                // 移除实体
                target.discard();

                // 替换物品
                player.setItemInHand(hand, newNet);

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    // 客户端模型切换
    public static float getHasEntity(ItemStack stack) {
        return hasEntity(stack) ? 1.0F : 0.0F;
    }

    // 获取实体类型，用于模型切换
    public static String getEntityType(ItemStack stack) {
        if (!hasEntity(stack)) {
            return "none";
        }
        CompoundTag entityData = getEntityData(stack);
        if (entityData == null) {
            return "none";
        }
        return entityData.getString("id");
    }

    // 获取实体类型的简短名称，用于模型切换
    public static float getEntityTypeForModel(ItemStack stack) {
        String entityType = getEntityType(stack);
        switch (entityType) {
            case "minecraft:cow":
                return 1.0F; // 水母
            case "minecraft:chicken":
                return 2.0F; // 蓝水母
            case "minecraft:sheep":
                return 3.0F; // 泡泡水母
            case "minecraft:pig":
                return 4.0F; // 奶牛水母
            case "minecraft:wolf":
                return 5.0F; // 油脂水母
            case "minecraft:fox":
                return 6.0F; // 双拳跳跳水母
            default:
                return 0.0F;
        }
    }
}
