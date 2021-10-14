package dev.sl4sh.feather.items.debug;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.client.rendering.linerenderer.Line;
import dev.sl4sh.feather.client.rendering.linerenderer.LineRenderer;
import dev.sl4sh.feather.util.Utilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class LineDrawer extends Item {
    public LineDrawer() {
        super(new Settings().group(Feather.FEATHER_ITEM_GROUP));
    }

    public static BlockHitResult drawRaycast(World world, PlayerEntity player, RaycastContext.FluidHandling fluidHandling) {
        BlockHitResult result = raycast(world, player, fluidHandling);
        if(world.isClient()){
            LineRenderer.INSTANCE.drawLine(new Line(Utilities.toVec3f(player.getEyePos()), Utilities.toVec3f(result.getBlockPos()), Line.Color.random(), 1.0f));
        }
        return result;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        drawRaycast(world, user, RaycastContext.FluidHandling.ANY);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
