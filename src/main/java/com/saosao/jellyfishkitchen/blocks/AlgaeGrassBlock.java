package com.saosao.jellyfishkitchen.blocks;

import com.saosao.jellyfishkitchen.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 藻类草方块逻辑类
 * 优化点：
 * 1. 移除了 getDrops，由外部 JSON 战利品表控制。
 * 2. 实现了即时检测：当上方被覆盖时，立即退化为珊瑚石。
 */
public class AlgaeGrassBlock extends Block {

    public AlgaeGrassBlock(Properties properties) {
        super(properties);
    }

    /**
     * 当方块【自己】被放置时触发。
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        // 放置一瞬间就检查上方环境
        this.tryConvertToCoralstone(level, pos);
    }

    /**
     * 当【周围方块】发生变化时触发（例如有人在你头顶放了东西）。
     */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        // 如果变化发生在你正上方，立即执行转换逻辑
        if (neighborPos.equals(pos.above())) {
            this.tryConvertToCoralstone(level, pos);
        }
    }

    /**
     * 随机刻触发（模拟原版草变泥土的自然退化）。
     * 注意：1.21 使用 RandomSource 而不是 java.util.Random。
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 即使没有邻居更新，随机刻也会确保环境不适时方块会退化
        this.tryConvertToCoralstone(level, pos);
    }

    /**
     * 核心逻辑：尝试将当前方块转换回珊瑚石。
     * 判断标准：如果上方方块不透明（Occlude）或不是空气。
     */
    private void tryConvertToCoralstone(Level level, BlockPos pos) {
        // 所有修改世界的逻辑必须在服务端运行
        if (!level.isClientSide()) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            // canOcclude() 判断该方块是否遮挡视线/光线，这是模仿原版草方块死亡的最精准方法
            // 如果你希望更严格（只要不是空气就变石头），可以将条件改为 !aboveState.isAir()
            if (aboveState.canOcclude()) {
                // 使用 setBlockAndUpdate 确保更新能够同步到客户端并触发方块更新
                level.setBlockAndUpdate(pos, ModBlocks.CORALSTONE.get().defaultBlockState());
            }
        }
    }
}