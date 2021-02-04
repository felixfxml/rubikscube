package rubikscube.opengl;

import org.joml.Matrix4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int id;

    public Shader(String name) {
        id = glCreateProgram();
        int vert = glCreateShader(GL_VERTEX_SHADER);
        String vertSource = fileSource("shaders/" + name + ".vert");
        glShaderSource(vert, vertSource);
        glCompileShader(vert);

        if (glGetObjectParameteriARB(vert, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
            throw new RuntimeException("failed creating shader: " + log(vert));

        int frag = glCreateShader(GL_FRAGMENT_SHADER);
        String fragSource = fileSource("shaders/" + name + ".frag");
        glShaderSource(frag, fragSource);
        glCompileShader(frag);

        if (glGetObjectParameteriARB(frag, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
            throw new RuntimeException("failed creating shader: " + log(frag));

        glAttachShader(id, vert);
        glAttachShader(id, frag);

        glDeleteShader(vert);
        glDeleteShader(frag);

        glLinkProgram(id);

        System.out.println("successfully created a shader program with the id " + id);

    }

    private String log(int shader) {
        return glGetInfoLogARB(shader, glGetObjectParameteriARB(shader, GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void delete() {
        glDeleteProgram(id);
    }

    public void uniform(String name, float value) {
        int location = glGetUniformLocation(id, name);
        glUniform1f(location, value);
    }

    public void uniform(String name, float x, float y, float z) {
        int location = glGetUniformLocation(id, name);
        glUniform3f(location, x, y, z);
    }

    public void uniform(String name, float x, float y, float z, float w) {
        int location = glGetUniformLocation(id, name);
        glUniform4f(location, x, y, z, w);
    }

    public void uniform(String name, int x, int y) {
        int location = glGetUniformLocation(id, name);
        glUniform2i(location, x, y);
    }

    public void uniform(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(id, name);
        float[] f = new float[16];
        glUniformMatrix4fv(location, false, matrix.get(f));
    }

    public String fileSource(String ref) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/" + ref);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder source = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source.toString();
    }

}
