package dev.compactmods.machines.upgrade.workbench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class RoomUpgradeWorkbench extends Block implements EntityBlock {
    public RoomUpgradeWorkbench(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RoomUpgradeWorkbenchEntity(pPos, pState);
    }
}
