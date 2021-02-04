package rubikscube.opengl;

import rubikscube.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

    private int id;
    private Color currentColor;
    private Vertex currentVertex;

    private List<Vertex> vertices;

    public VertexBuffer() {
        id = glGenBuffers();
        currentVertex = null;
        currentColor = null;
        vertices = new ArrayList<>();
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

    public VertexBuffer vertex3d(float x, float y, float z) {
        currentVertex = new Vertex(x, y, z, 1, currentColor);
        return this;
    }

    public VertexBuffer vertex2d(float x, float y) {
        return vertex3d(x, y, 0);
    }

    public VertexBuffer color(float r, float g, float b, float a) {
        currentColor = new Color(r, g, b, a);
        currentVertex.setColor(currentColor);
        return this;
    }

    public VertexBuffer color(float r, float g, float b) {
        return color(r, g, b, 1.0f);
    }

    public VertexBuffer color(int hex) {
        float[] c = ColorUtil.hexToRGB(hex);
        return color(c[0], c[1], c[2], c[3]);
    }

    public void endVertex() {
        vertices.add(currentVertex);
    }

    public void end() {

        float[] data = new float[vertices.size() * 8];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vert = vertices.get(i);
            for (int i1 = 0; i1 < vert.data().length; i1++) {
                data[i * vert.data().length + i1] = vert.data()[i1];
            }
        }
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

}
