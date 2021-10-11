package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.IPreMineInterface;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IPreMineInterface {
    @Shadow
    public Item getItem() {
        return null;
    }

    @Override
    public void preMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {

        IPreMineInterface preMine = Utilities.as(getItem());
        preMine.preMine(world, state, pos, miner);

    }

}
