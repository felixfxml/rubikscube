package rubikscube.opengl;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class ElementArrayBuffer {

    private int id;
    private List<Integer> indices;

    public ElementArrayBuffer() {
        id = glGenBuffers();
        indices = new ArrayList<>();
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    }

    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

    public void addIndex(int i) {
        indices.add(i);
    }

    public void addTriangle(int a, int b, int c) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
    }

    public void end() {
        int[] indices = new int[this.indices.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = this.indices.get(i);
        }
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
    }

    public void clear() {
        indices.clear();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[0], GL_DYNAMIC_DRAW);
    }

    public int getSize() {
        return indices.size();
    }
}
