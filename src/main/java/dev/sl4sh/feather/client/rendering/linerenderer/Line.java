package dev.sl4sh.feather.client.rendering.linerenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class Line {

    public interface Color{

        Vector4f RED = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
        Vector4f GREEN = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
        Vector4f BLUE = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
        Vector4f BLACK = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        static Vector4f random(){

            Random random = new Random();

            return new Vector4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);

        }

    }

    private final Vec3f start;
    private final Vec3f end;
    private final Vector4f drawColor;
    private final float width;

    public Line(Vec3f start, Vec3f end, Vector4f drawColor, float width) {
        this.start = start;
        this.end = end;
        this.drawColor = drawColor;
        this.width = width;
    }

    public Vec3f getStart() {
        return start;
    }

    public Vec3f getEnd() {
        return end;
    }

    public Vector4f getDrawColor() {
        return drawColor;
    }

    public float getWidth() {
        return width;
    }
}
