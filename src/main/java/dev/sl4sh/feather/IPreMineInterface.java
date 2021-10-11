package dev.sl4sh.feather;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPreMineInterface {

    void preMine(World world, BlockState state, BlockPos pos, PlayerEntity miner);

}
