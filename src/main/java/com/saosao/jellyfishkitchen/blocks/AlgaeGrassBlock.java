package com.saosao.jellyfishkitchen.blocks;

import com.saosao.jellyfishkitchen.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AlgaeGrassBlock extends Block {

    public AlgaeGrassBlock(Properties properties) {
        super(properties);
    }

    /**
     * 当藻类草方块【自己】被放下去时检查一次
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        this.tryConvertToCoralstone(level, pos);
    }

    /**
     * 当【周围方块】发生变化（比如有人在你头上放了块石头）时触发
     */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        // 如果变化发生在你【正上方】，就检查一下
        if (neighborPos.equals(pos.above())) {
            this.tryConvertToCoralstone(level, pos);
        }
    }

    /**
     * 核心逻辑：检查上方环境，如果不合格就变回珊瑚石
     */
    private void tryConvertToCoralstone(Level level, BlockPos pos) {
        if (!level.isClientSide()) { // 只在服务端执行，防止同步错误
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            // 如果上方不是空气，或者是实心方块（可以根据需要调整判断条件）
            if (!aboveState.isAir() && aboveState.isSolidRender(level, abovePos)) {
                // 将自己替换为珊瑚石
                level.setBlockAndUpdate(pos, ModBlocks.CORALSTONE.get().defaultBlockState());
            }
        }
    }
}