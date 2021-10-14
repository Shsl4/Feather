package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.IPreMineInterface;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow @Mutable
    protected ServerWorld world;

    @Final @Shadow @Mutable
    protected ServerPlayerEntity player;

    @Shadow @Mutable
    private GameMode gameMode;

    @Inject(at = @At(value = "HEAD"), method = "tryBreakBlock", cancellable = true)
    public void tryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        BlockState blockState = this.world.getBlockState(pos);
        if (!this.player.getMainHandStack().getItem().canMine(blockState, this.world, pos, this.player)) {
            info.setReturnValue(false);
        } else {
            BlockEntity blockEntity = this.world.getBlockEntity(pos);
            Block block = blockState.getBlock();
            if (block instanceof OperatorBlock && !this.player.isCreativeLevelTwoOp()) {
                this.world.updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL);
                info.setReturnValue(false);
            } else if (this.player.isBlockBreakingRestricted(this.world, pos, this.gameMode)) {
                info.setReturnValue(false);
            } else {

                if (!this.gameMode.isCreative()) {
                    ItemStack itemStack = this.player.getMainHandStack();
                    IPreMineInterface i = Utilities.as(itemStack);
                    i.preMine(this.world, blockState, pos, this.player);
                }

                block.onBreak(this.world, pos, blockState, this.player);
                boolean bl = this.world.removeBlock(pos, false);
                if (bl) {
                    block.onBroken(this.world, pos, blockState);
                }

                if (!this.gameMode.isCreative()) {
                    ItemStack itemStack = this.player.getMainHandStack();
                    ItemStack itemStack2 = itemStack.copy();
                    boolean bl2 = this.player.canHarvest(blockState);
                    itemStack.postMine(this.world, blockState, pos, this.player);
                    if (bl && bl2) {
                        block.afterBreak(this.world, this.player, pos, blockState, blockEntity, itemStack2);
                    }

                }
                info.setReturnValue(true);
            }
        }

        info.cancel();

    }

}
