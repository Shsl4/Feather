package dev.sl4sh.feather.mixin;

import dev.sl4sh.feather.Feather;
import dev.sl4sh.feather.event.world.WorldDebugRenderEvent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.impl.client.rendering.WorldRenderContextImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class DebugRendererMixin {

    @Final @Shadow private BufferBuilderStorage bufferBuilders;
    @Shadow private ClientWorld world;
    @Shadow private ShaderEffect transparencyShader;
    @Final @Shadow private MinecraftClient client;
    @Unique private final WorldRenderContextImpl context = new WorldRenderContextImpl();
    @Unique private boolean didRenderParticles;

    @Inject(method = "render", at = @At("HEAD"))
    private void beforeRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        context.prepare((WorldRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f, bufferBuilders.getEntityVertexConsumers(), world.getProfiler(), transparencyShader != null, world);
        didRenderParticles = false;
    }

    @Inject(method = "setupTerrain", at = @At("RETURN"))
    private void afterTerrainSetup(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator, CallbackInfo ci) {
        context.setFrustum(frustum);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onDrawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (!context.renderBlockOutline) {

            // Was cancelled before we got here, so do not
            // fire the BLOCK_OUTLINE event per contract of the API.
            ci.cancel();
        } else {
            context.prepareBlockOutline(entity, cameraX, cameraY, cameraZ, blockPos, blockState);

            // The immediate mode VertexConsumers use a shared buffer, so we have to make sure that the immediate mode VCP
            // can accept block outline lines rendered to the existing vertexConsumer by the vanilla block overlay.
            context.consumers().getBuffer(RenderLayer.getLines());
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/debug/DebugRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V",
                    ordinal = 0
            )
    )
    private void beforeDebugRender(CallbackInfo ci) {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(context);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V"
            )
    )
    private void onRenderParticles(CallbackInfo ci) {
        didRenderParticles = true;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void beforeClouds(CallbackInfo ci) {
        if (didRenderParticles) {
            didRenderParticles = false;
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void preRender(CallbackInfo ci) {
        WorldDebugRenderEvent event = new WorldDebugRenderEvent(context);
        Feather.getEventRegistry().DEBUG_RENDER.invoke(event);
    }


}
