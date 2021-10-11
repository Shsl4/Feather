package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.IPreMineInterface;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements IPreMineInterface {

    @Override
    public void preMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {

    }

}
