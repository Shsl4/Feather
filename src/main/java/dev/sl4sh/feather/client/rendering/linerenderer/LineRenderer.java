package dev.sl4sh.feather.client.rendering.linerenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@Environment(EnvType.CLIENT)
public final class LineRenderer implements DebugRenderer.Renderer {

    private final ArrayList<Line> renderedLines = new ArrayList<>();
    private final ArrayList<Line> pendingRemove = new ArrayList<>();
    public static final LineRenderer INSTANCE = new LineRenderer();

    @Override
    public void clear() {
        DebugRenderer.Renderer.super.clear();
    }

    private LineRenderer(){

    }

    private static TimerTask task(Runnable r) {
        return new TimerTask() {

            @Override
            public void run() {
                r.run();
            }
        };
    }

    public void drawLine(Line line){
        drawLine(line, 10000);
    }

    public void drawLine(Line line, long ms){

        renderedLines.add(line);
        if (ms <= 0){
            ms = 10000;
        }

        new Timer().schedule(task(() -> {

            pendingRemove.add(line);

        }), ms);

    }

    public void clearLines(){
        renderedLines.clear();
    }

    // https://github.com/Sam54123/scaffold-editor-mc/blob/a83d7b41b27aa553bcb8be501f94fb7919bd6a22/src/main/java/org/scaffoldeditor/editormc/engine/RenderUtils.java
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {

        matrices.push();
        matrices.translate(-cameraX, -cameraY, -cameraZ);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());

        synchronized (renderedLines) {

            for (Line line : pendingRemove){
                renderedLines.remove(line);
            }

            pendingRemove.clear();

            for (Line line : renderedLines) {

                Matrix4f model = matrices.peek().getPositionMatrix();
                Matrix3f normal = matrices.peek().getNormalMatrix();
                Vec3f start = line.getStart();
                Vec3f end = line.getEnd();
                Vector4f color = line.getDrawColor();
                Vec3f diff = new Vec3f(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());

                consumer.vertex(model, start.getX(), start.getY(), start.getZ())
                        .color(color.getX(), color.getY(), color.getZ(), color.getW())
                        .normal(normal, diff.getX(), diff.getY(), diff.getZ()).next();

                consumer.vertex(model, end.getX(), end.getY(), end.getZ())
                        .color(color.getX(), color.getY(), color.getZ(), color.getW())
                        .normal(normal, diff.getX(), diff.getY(), diff.getZ()).next();

            }
        }

        matrices.pop();

    }

}
