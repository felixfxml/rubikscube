package learnopengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import rubikscube.opengl.Camera;
import rubikscube.opengl.VertexBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CubeWindow {

    public int vao, program;
    long window;
    int ebo;
    private Camera camera;

    public static void main(String[] args) {
        new CubeWindow().run();
    }

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(800, 600, "LeanOpenGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        glViewport(0, 0, 800, 600);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> glViewport(0, 0, width, height));

        try {
            prepareCube();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //wireframe:
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        camera = new Camera(new Vector3f(10, 1, 0), new Vector3f(0, 0, 0));

        glEnable(GL_DEPTH_TEST);

        while (!glfwWindowShouldClose(window)) { //checks if we told GLFW to close
            processInput();

            //rendering commands
            glClearColor(.2f, .3f, .3f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glBindVertexArray(vao);
            glUseProgram(program);

            Vector3f camPos = new Vector3f((float) java.lang.Math.sin(glfwGetTime() * .5) * 4f, 1f, (float) java.lang.Math.cos(glfwGetTime() * .5) * 4f);
            camera.setPosition(camPos);
            camera.update();

            Matrix4f projection = new Matrix4f().perspective(45, 800f / 600f, .1f, 100f);
            Matrix4f view = camera.getView();
            Matrix4f model = new Matrix4f().rotate((float) glfwGetTime() * .5f, 1, 0, 0).scale(.5f);

            float[] mat = new float[16];
            glUniformMatrix4fv(glGetUniformLocation(program, "projection"), false, projection.get(mat));
            glUniformMatrix4fv(glGetUniformLocation(program, "view"), false, view.get(mat));
            glUniformMatrix4fv(glGetUniformLocation(program, "model"), false, model.get(mat));

            //glDrawArrays(GL_TRIANGLES, 0, 36);
            int count = 3 * 2 * 6;
            glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window); //swaps the color buffer (contains all color values for each pixel) used in this render iteration and shows it on the screen
            glfwPollEvents(); //checks if any events are triggered (like keyboard input or mouse movement events), updates window state, calls callback functions
        }

        glfwTerminate();

    }

    public void prepareCube() throws IOException {
        float[] vertices = new float[]{
                //vertices            //colors
                1f, 1f, 1f, 1, 0, 0,         //front top right (index 0
                1f, -1f, 1f, 0, 1, 0,            //front bottom right (index 1)
                -1f, -1f, 1f, 0, 0, 1,            //front bottom right (index 2)
                -1f, 1f, 1f, 1, 1, 0,            //front top left (index 3)
                1f, 1f, -1f, 1, 0, 1,            //back top right (index 4)
                1f, -1f, -1f, 0, 1, 1,            //back bottom right (index 5)
                -1f, -1f, -1f, 1, 1, 1,          //back bottom right (index 6)
                -1f, 1f, -1f, 0, 0, 0,            //back top left (index 7)
        };

        int[] indices = new int[]{
                0, 1, 3,
                1, 2, 3,

                4, 5, 7,
                5, 6, 7,

                0, 1, 5,
                0, 5, 4,

                2, 3, 7,
                2, 7, 6,

                0, 3, 7,
                0, 7, 4,

                1, 2, 6,
                1, 6, 5

        };
        //generating VBOs works as following:
        //int vbo = glGenBuffers();

        //the buffer needs to be bound as an GL_ARRAY_BUFFER
        //glBindBuffer(GL_ARRAY_BUFFER, vbo);

        //all calls regarding GL_ARRAY_BUFFER will be used to configure our currently bound vbo
        //possible forms for the 3rd parameter:
        //GL_STREAM_DRAW: data is set once and used by the GPU at most a few times
        //GL_STATIC_DRAW: data is set once and used many times
        //GL_DYNAMIC_DRAW: data is changed a lot and used many times
        //glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        {
            //vertex shader
            //GLSL: shader begins with declaration of its version (we use version 330 + "core" to mention we use the core profile functionality)
            //declare input vertex attributes using the 'in' keyword
            //GLSL supports vectors with 1 to 4 dimensions which contain 1 to 4 floats (declaration: vec1 example = vec1(0.0f) or vec4 _example = vec4(0.0f, 0.0f, 0.0f, 0.0f))
            //useful: bigger vectors can be defined using smaller ones like this:
            //vec4 a = vec4(any vec3, 0.0f);

            //get the shader source
            String vertSource = fileSource("learnopengl/cube.vert");

            //create a shader object again using an ID
            int vert = glCreateShader(GL_VERTEX_SHADER);

            glShaderSource(vert, vertSource);
            glCompileShader(vert);

            String fragSource = fileSource("learnopengl/cube.frag");

            int frag = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(frag, fragSource);
            glCompileShader(frag);

            program = glCreateProgram();
            glAttachShader(program, vert);
            glAttachShader(program, frag);
            glLinkProgram(program);

            glDeleteShader(vert);
            glDeleteShader(frag);

        }

        VertexBuffer vbo = new VertexBuffer();
        vbo.bind();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        //vertices
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        //colors
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //actual rendering (in the render loop)
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        //glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    public String fileSource(String ref) throws IOException {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/" + ref);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder source = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            source.append(line).append('\n');
        }
        return source.toString();
    }

    public void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

}
