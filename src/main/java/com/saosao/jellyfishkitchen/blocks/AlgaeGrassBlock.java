package com.saosao.jellyfishkitchen.blocks;

import com.saosao.jellyfishkitchen.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AlgaeGrassBlock extends Block {

    public AlgaeGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        this.tryConvertToCoralstone(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (neighborPos.equals(pos.above())) {
            this.tryConvertToCoralstone(level, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.tryConvertToCoralstone(level, pos);
    }

    private void tryConvertToCoralstone(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            // 逻辑：如果上方不是空气 且 会遮挡视线（实心），则变回珊瑚石
            if (!aboveState.isAir() && aboveState.canOcclude()) {
                level.setBlockAndUpdate(pos, ModBlocks.CORALSTONE.get().defaultBlockState());
            }
        }
    }
}