package learnopengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL;

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

public class TriangleWindow {

    public int vao, program;
    long window;

    public static void main(String[] args) {
        new TriangleWindow().run();
    }

    public void run() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //create window object
        window = glfwCreateWindow(800, 600, "LeanOpenGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        glViewport(0, 0, 800, 600);

        //set framebuffer size callback (adjusting the viewport when the window is resized)
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> glViewport(0, 0, width, height));

        try {
            prepareTriangle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //wireframe:
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        //init render loop
        while (!glfwWindowShouldClose(window)) { //checks if we told GLFW to close

            //input
            processInput();

            //rendering commands
            glClearColor(.2f, .3f, .3f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT);//clears the color buffer using the color specified before

            glBindVertexArray(vao);
            glUseProgram(program);
            /*Matrix4f trans2 = new Matrix4f();
            trans2.setIdentity();
            trans2.setRotation(new Quat4f(0, 0, 1, (float) java.lang.Math.toRadians(90.0f)));
            trans2.setScale(0.5f);

            int loc = glGetUniformLocation(program, "transform");
            glUniformMatrix4fv(loc, false, mat4toFloatBuffer(trans2));*/

            Matrix4f transform = new Matrix4f().identity();
            //transform.translate(0.5f,0,0);
            //transform.scale(0.5f);
            Vector3f rotationVector = new Vector3f(0, 1, 0).normalize();
            transform.rotate((float) glfwGetTime() * 2, rotationVector);

            int loc = glGetUniformLocation(program, "transform");
            float[] mat = new float[16];
            glUniformMatrix4fv(loc, false, transform.get(mat));

            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window); //swaps the color buffer (contains all color values for each pixel) used in this render iteration and shows it on the screen
            glfwPollEvents(); //checks if any events are triggered (like keyboard input or mouse movement events), updates window state, calls callback functions
        }

        glfwTerminate();

    }

    public void prepareTriangle() throws IOException {
        //OpenGL coordinates are normalized (range from -1(left/bottom) to 1(right/top))
        //define vertex data
        float[] vertices = new float[]{
                -.5f, -.5f, .0f,
                .5f, -.5f, .0f,
                .0f, .5f, .0f
        };

        //send it as input to the first process of the graphics pipeline: vertex shader
        //create memory on the GPU
        //manage the memory using vertex buffer objects (VBOs)
        //advantage: large batches of data
        //sending data from the CPU to the GPU is slow so we want to send as much as possible at once

        //generating VBOs works as following:
        //int vbo = glGenBuffers();

        //or if you want more than a single one:

        //IntBuffer vbo = IntBuffer.allocate(1);
        //glGenBuffers(vbo);

        //the buffer needs to be bound as an GL_ARRAY_BUFFER
        //glBindBuffer(GL_ARRAY_BUFFER, vbo);

        //all calls regarding GL_ARRAY_BUFFER will be used to configure our currently bound vbo
        //possible forms for the 3rd parameter:
        //GL_STREAM_DRAW: data is set once and used by the GPU at most a few times
        //GL_STATIC_DRAW: data is set once and used many times
        //GL_DYNAMIC_DRAW: data is changed a lot and used many times
        //glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        //vertex shader
        //GLSL: shader begins with declaration of its version (we use version 330 + "core" to mention we use the core profile functionality)
        //declare input vertex attributes using the 'in' keyword
        //GLSL supports vectors with 1 to 4 dimensions which contain 1 to 4 floats (declaration: vec1 example = vec1(0.0f) or vec4 _example = vec4(0.0f, 0.0f, 0.0f, 0.0f))
        //useful: bigger vectors can be defined using smaller ones like this:
        //vec4 a = vec4(any vec3, 0.0f);

        //get the shader source
        String vertSource = fileSource("learnopengl/shader.vert");

        //create a shader object again using an ID
        int vert = glCreateShader(GL_VERTEX_SHADER);

        //attach the shader source code, OpenGL compiles it dynamically at run-time
        glShaderSource(vert, vertSource);
        glCompileShader(vert);

        //fragment shader
        //is about calculating the color output of pixels
        //colors are represented as a vec4 (rgba - each with a range from 0 to 1)
        //only requires one output variable

        //get the shader source again
        String fragSource = fileSource("learnopengl/shader.frag");

        //create a shader object, attach source, compile
        int frag = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(frag, fragSource);
        glCompileShader(frag);

        //both shaders are compiled and now need to be linked in a program
        //when linking the shaders, the program will link their inputs and outputs
        //there will be errors if the inputs and outputs don't match

        //create a program
        program = glCreateProgram();
        //attach the shaders and link the program
        glAttachShader(program, vert);
        glAttachShader(program, frag);
        glLinkProgram(program);

        //activate the program using glUseProgram(program)

        //delete the shaders as they are already linked
        glDeleteShader(vert);
        glDeleteShader(frag);

        //linking vertex attributes
        //we can specify the input in the form of vertex attributes for the vertex shader
        //-> great flexibility, but we have to specify what part of our input data goes to which vertex attribute
        //(we have to specify how OpenGL should interpret the data before rendering)

        //data in the vertex buffer (vbo) is stored as this:
        //each position is stored as 4 bytes (32 bit - float)
        //so one vertex is 12 bytes (3 positions)

        //glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);

        //index is set to 0 which we specified in the vertex shader (location=0),
        //size is set to 3 because the position is represented as a vec3
        //data type is GL_FLOAT because vectors in glsl hold float values
        //normalized is only useful when inputting integers or bytes, it will normalize them to fit in a range from -1 to 1
        //however we already pass floats so we set it to false
        //the stride is the space between consecutive vertex attributes
        //the next vertex is 3 times the size of a float (32 bits = 4 bytes) away
        //the data is tightly packed (no space between data), so stride could be set to 0 to let OpenGL determine it on its own
        //pointer is the offset of where the position data begins in the buffer, it's set to 0 because we start right at the beginning

        //OpenGL now knows how to interpret the data, now the vertex attribute is enabled because they are disabled by default
        //glEnableVertexAttribArray(0);

        //Vertex Array Objects (VAOs):
        //can be bound just like a VBO
        //any vertex attribute calls will be stored in the VAO
        //advantage: when configuring vertex attribute pointers the calls have to be made only once, after that when the object needs to be drawn just bind the VAO
        //using core OpenGL without VAO there will be nothing drawn
        //VAOs store:
        //-call to enable and disable vertex attribute array
        //-vertex attribute configurations (glVertexAttribPointer)
        //-VBOs associated with vertex attributes (glVertexAttribPointer)

        //creating one works as always
        vao = glGenVertexArrays();
        int vbo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0);

        //rendering 2 triangles to form a rectangle
        //vertices would overlap
        //so we use element buffer objects
        //specify unique vertices (attach them to the VBO)
        //specify the indices

        //float[] vertices = new float[]{
        //        .5f, .5f, .0f, //top right (index 0)
        //        .5f, -.5f, .0f, //bottom right (index 1)
        //        -.5f, -.5f, .0f, //bottom right (index 2)
        //        -.5f, .5f, .0f //top left (index 3)
        //};

        //int[] indices = new int[]{
        //        0, 1, 3, //first triangle
        //        1, 2, 3 //second triangle
        //};

        //now we only need 4 vertices instead of 6

        //int ebo = glGenBuffers();
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

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
