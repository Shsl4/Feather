package dev.sl4sh.feather.items.tools;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.IPreMineInterface;
import dev.sl4sh.feather.items.debug.LineDrawer;
import dev.sl4sh.feather.items.tools.materials.HammerToolMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class HammerTool extends PickaxeItem implements IPreMineInterface {

    public HammerTool() {

        super(HammerToolMaterial.INSTANCE,3, -3.0f, new Item.Settings().group(Feather.FEATHER_ITEM_GROUP));

    }

    private static void destroyBlock(ItemStack stack, ServerWorld world, ServerPlayerEntity player, BlockPos pos){

        BlockEntity blEntity = world.getBlockEntity(pos);
        BlockState blState = world.getBlockState(pos);
        Block block = blState.getBlock();

        boolean canHarvest = player.canHarvest(blState);

        if(canHarvest){

            block.onBreak(world, pos, blState, player);

            boolean blockRemoved = world.removeBlock(pos, false);
            if (blockRemoved) {
                block.onBroken(world, pos, blState);
            }

            block.afterBreak(world, player, pos, blState, blEntity, stack);

        }

    }

    private static void harvestBlocks(ItemStack stack, ServerWorld world, ServerPlayerEntity player, BlockPos pos){

        BlockHitResult hit = LineDrawer.drawRaycast(world, player, RaycastContext.FluidHandling.ANY);

        Direction direction = hit.getSide();
        Set<BlockPos> positions = new HashSet<>();

        if(direction == Direction.UP || direction == Direction.DOWN){

            positions.add(pos.north());
            positions.add(pos.south());
            positions.add(pos.east());
            positions.add(pos.west());
            positions.add(pos.north().east());
            positions.add(pos.north().west());
            positions.add(pos.south().east());
            positions.add(pos.south().west());

        }

        if(direction == Direction.EAST || direction == Direction.WEST){

            positions.add(pos.up());
            positions.add(pos.down());
            positions.add(pos.north());
            positions.add(pos.south());
            positions.add(pos.up().north());
            positions.add(pos.up().south());
            positions.add(pos.down().north());
            positions.add(pos.down().south());

        }

        if(direction == Direction.NORTH || direction == Direction.SOUTH){

            positions.add(pos.up());
            positions.add(pos.down());
            positions.add(pos.east());
            positions.add(pos.west());
            positions.add(pos.up().east());
            positions.add(pos.up().west());
            positions.add(pos.down().east());
            positions.add(pos.down().west());

        }

        for (BlockPos position : positions){
            destroyBlock(stack, world, player, position);
        }


    }

    @Override
    public void preMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {

        if(!world.isClient()){
            harvestBlocks(miner.getMainHandStack(), (ServerWorld)world, (ServerPlayerEntity)miner, pos);
        }

    }

}
