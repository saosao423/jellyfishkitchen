package com.saosao.jellyfishkitchen.item;

import com.saosao.jellyfishkitchen.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.Optional;

public class JellyfishNetItem extends Item {

    public JellyfishNetItem(Properties properties) {
        super(properties);
    }

    // --- 数据处理逻辑 ---

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

    // --- 核心交互逻辑 ---

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (hasEntity(context.getItemInHand())) {
            return releaseEntity(context.getPlayer(), context.getLevel(), context.getItemInHand(),
                    context.getClickedPos(), context.getClickedFace(), context.getHand());
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hasEntity(stack)) {
            InteractionResult result = releaseEntity(player, level, stack, null, null, hand);
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(stack);
    }

    /**
     * 统一的实体释放逻辑
     */
    private InteractionResult releaseEntity(Player player, Level level, ItemStack stack, BlockPos pos, Direction face, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        CompoundTag entityData = getEntityData(stack);
        if (entityData == null) return InteractionResult.PASS;

        // 获取实体类型并生成
        return EntityType.byString(entityData.getString("id")).map(type -> {
            Entity entity = type.create(serverLevel);
            if (entity != null) {
                entity.load(entityData);

                // 计算生成位置：如果点了方块则放在方块面中心，否则放在玩家面前
                Vec3 spawnPos;
                if (pos != null && face != null) {
                    spawnPos = Vec3.atBottomCenterOf(pos).relative(face, 0.5);
                } else {
                    spawnPos = player.position().add(player.getLookAngle().scale(1.5)).add(0, 1, 0);
                }

                entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0);

                if (serverLevel.addFreshEntity(entity)) {
                    // 播放音效与事件
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos);

                    // 转换为空网并处理耐久
                    ItemStack resultStack = new ItemStack(ModItems.JELLYFISH_NET.get());
                    resultStack.setDamageValue(stack.getDamageValue());

                    if (!player.getAbilities().instabuild) {
                        resultStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }

                    player.setItemInHand(hand, resultStack);
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hasEntity(stack) || player.getCooldowns().isOnCooldown(this)) return InteractionResult.PASS;

        // 捕捉白名单：牛、鸡、羊、猪、狼、狐狸
        if (target instanceof Cow || target instanceof Chicken || target instanceof Sheep ||
                target instanceof Pig || target instanceof Wolf || target instanceof Fox) {

            player.swing(hand);

            if (!player.level().isClientSide() && target.isAlive()) {
                player.getCooldowns().addCooldown(this, 20);
                player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.NEUTRAL, 1.0F, 1.0F);

                // 核心：保存 NBT 并手动注入 ID (用于 1.21 识别)
                CompoundTag entityData = new CompoundTag();
                target.save(entityData);
                entityData.putString("id", EntityType.getKey(target.getType()).toString());

                ItemStack newNet = new ItemStack(ModItems.JELLYFISH_NET.get());
                newNet.setDamageValue(stack.getDamageValue());
                storeEntityData(newNet, entityData);

                // 耐久度处理
                if (!player.getAbilities().instabuild) {
                    newNet.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }

                target.discard();
                player.setItemInHand(hand, newNet);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // --- 模型属性接口 ---

    public static float getHasEntity(ItemStack stack) {
        return hasEntity(stack) ? 1.0F : 0.0F;
    }

    public static float getEntityTypeForModel(ItemStack stack) {
        if (!hasEntity(stack)) return 0.0F;
        CompoundTag data = getEntityData(stack);
        if (data == null) return 0.0F;

        String id = data.getString("id");
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