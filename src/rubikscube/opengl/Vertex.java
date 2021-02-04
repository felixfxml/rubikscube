package rubikscube.opengl;

import org.joml.Vector4f;

public class Vertex {

    private Vector4f pos;

    private Color color;

    public Vertex(float x, float y, float z, float w, Color color) {
        pos = new Vector4f(x, y, z, w);
        this.color = color;
    }

    public float getX() {
        return pos.x();
    }

    public float getY() {
        return pos.y();
    }

    public float getZ() {
        return pos.z();
    }

    public float getW() {
        return pos.w();
    }

    public Color getColor() {
        return color == null ? new Color(1, 1, 1, 1) : color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getRed() {
        return getColor().getRed();
    }

    public float getGreen() {
        return getColor().getGreen();
    }

    public float getBlue() {
        return getColor().getBlue();
    }

    public float getAlpha() {
        return getColor().getAlpha();
    }

    public float[] data() {
        return new float[]{getX(), getY(), getZ(), getW(), getRed(), getGreen(), getBlue(), getAlpha()};
    }

}
