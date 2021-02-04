package rubikscube.opengl;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

    private int id;

    public VertexArray() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }

    public void addVertexBuffer(VertexBuffer vbo) {
        bind();
        vbo.bind();

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 8 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 8 * Float.BYTES, 4 * Float.BYTES);
    }

    public void setIndexBuffer(ElementArrayBuffer ebo) {
        bind();
        ebo.bind();
    }

}
