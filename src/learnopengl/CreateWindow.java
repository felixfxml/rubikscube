package learnopengl;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CreateWindow {

    long window;

    public static void main(String[] args) {
        new CreateWindow().run();
    }

    public void run() {
        glfwInit();
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_VERSION_REVISION, 3); //Version 3.2.3

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

        //init render loop
        while (!glfwWindowShouldClose(window)) { //checks if we told GLFW to close

            //input
            processInput();

            //rendering commands
            glClearColor(.2f, .3f, .3f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT);//clears the color buffer using the color specified before

            glfwSwapBuffers(window); //swaps the color buffer (contains all color values for each pixel) used in this render iteration and shows it on the screen
            glfwPollEvents(); //checks if any events are triggered (like keyboard input or mouse movement events), updates window state, calls callback functions
        }

        glfwTerminate();

    }

    public void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

}
