package rubikscube;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import rubikscube.algorithms.Scramble;
import rubikscube.opengl.*;
import rubikscube.util.ColorUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static rubikscube.Block.*;

public class Main {

    public static final int CUBE_SIZE = 3;
    private double rotationStart;
    private long window;
    private int width, height;
    private VertexArray vao;
    private ElementArrayBuffer ebo;
    private VertexBuffer vbo;
    private Cube cube;
    private Camera camera;
    private Shader shader;
    private float rotationSpeed = 2f;
    private Scramble s;

    public Main(int width, int height) {
        setWidth(width);
        setHeight(height);
        cube = new Cube(CUBE_SIZE);
        camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        s = new Scramble(CUBE_SIZE * 100, cube);
    }

    public static void main(String[] args) {
        new Main(800, 600).run();
    }

    public void handleInput() {

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
        if (!cube.isRotating()) {
            s.next();
            rotationStart = glfwGetTime();
            rotationSpeed = 2f;
            if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
                cube.setRotationAxis(0);
                cube.setRotationColumn(0);
                rotationStart = glfwGetTime();
                cube.setRotating(true);
            }
            if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) {
                cube.setRotationAxis(1);
                cube.setRotationColumn(0);
                rotationStart = glfwGetTime();
                cube.setRotating(true);
            }
            if (glfwGetKey(window, GLFW_KEY_H) == GLFW_PRESS) {
                cube.setRotationAxis(2);
                cube.setRotationColumn(0);
                rotationStart = glfwGetTime();
                cube.setRotating(true);
            }
        }
    }

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();

        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(getWidth(), getHeight(), "Rubik's Cube", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        glViewport(0, 0, getWidth(), getHeight());

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
            setWidth(width);
            setHeight(height);
        });

        glSetup();

        glClearColor(.15f, .15f, .2f, 1.0f);

        while (!glfwWindowShouldClose(window)) {

            handleInput();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float distance = CUBE_SIZE * 5f;
            float speed = .3f;

            Vector3f camPos = new Vector3f((float) Math.sin(glfwGetTime() * speed) * distance, distance * .5f, (float) Math.cos(glfwGetTime() * speed) * distance);
            camera.setPosition(camPos);


            Matrix4f ortho = new Matrix4f().ortho(-CUBE_SIZE, CUBE_SIZE, -CUBE_SIZE, CUBE_SIZE, 0, CUBE_SIZE * CUBE_SIZE);
            Matrix4f projection = new Matrix4f().perspective(45, (float) getWidth() / getHeight(), .001f, distance * 2);
            Matrix4f view = camera.getView();

            vao.bind();
            shader.bind();
            shader.uniform("projection", projection);
            shader.uniform("view", view);

            renderCube();

            shader.unbind();
            vao.unbind();
            ebo.unbind();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();

    }

    public void glSetup() {
        shader = new Shader("cube");

        vao = new VertexArray();
        vao.bind();

        vbo = new VertexBuffer();
        vbo.bind();
        vbo.vertex3d(1f, 1f, 1f).endVertex();
        vbo.vertex3d(1f, -1f, 1f).endVertex();
        vbo.vertex3d(-1f, -1f, 1f).endVertex();
        vbo.vertex3d(-1f, 1f, 1f).endVertex();
        vbo.vertex3d(1f, 1f, -1f).endVertex();
        vbo.vertex3d(1f, -1f, -1f).endVertex();
        vbo.vertex3d(-1f, -1f, -1f).endVertex();
        vbo.vertex3d(-1f, 1f, -1f).endVertex();
        vbo.end();

        ebo = new ElementArrayBuffer();
        vao.addVertexBuffer(vbo);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1.0f);
    }

    public void renderCube() {
        for (int i = 0; i < CUBE_SIZE; i++) {
            for (int j = 0; j < CUBE_SIZE; j++) {
                for (int k = 0; k < CUBE_SIZE; k++) {
                    Block b = cube.getBlocks()[i][j][k];
                    if (b != null)
                        renderBlock(b);
                }
            }
        }
    }

    public void renderBlock(Block b) {
        for (int face = 0; face < b.getFaces().length; face++) {

            float offset = (CUBE_SIZE - 1) * cube.len * .5f;
            Matrix4f model = new Matrix4f();
            if (cube.isRotating()) {
                float angle = (float) Math.toRadians((glfwGetTime() - rotationStart) * 90.0f) * rotationSpeed;
                switch (cube.getRotationAxis()) {
                    case 0:
                        if (b.getPosition().x() == cube.getRotationColumn() * cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), 1, 0, 0);
                        break;
                    case 1:
                        if (b.getPosition().y() == cube.getRotationColumn() * cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), 0, 1, 0);
                        break;
                    case 2:
                        if (b.getPosition().z() == cube.getRotationColumn() * cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), 0, 0, 1);
                        break;
                }
                if (angle >= Math.toRadians(90)) {
                    cube.rotate(true);
                    cube.setRotating(false);
                }
            }

            model.translate(new Vector3f(b.getPosition()).mul(1.0f));
            shader.uniform("model", model);

            if (cube.isRotating()) {
                ebo.bind();
                switch (cube.getRotationAxis()) {
                    case 0:
                        switch (face) {
                            case RIGHT:
                                if (b.getPosition().x() != CUBE_SIZE - 1) {
                                    ebo.addIndex(1);
                                    ebo.addIndex(0);
                                    ebo.addIndex(4);
                                    ebo.addIndex(5);
                                }
                                break;
                            case LEFT:
                                if (b.getPosition().x() != -(CUBE_SIZE - 1)) {
                                    ebo.addIndex(3);
                                    ebo.addIndex(2);
                                    ebo.addIndex(6);
                                    ebo.addIndex(7);
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch (face) {
                            case UP:
                                if (b.getPosition().y() != CUBE_SIZE - 1) {
                                    ebo.addIndex(3);
                                    ebo.addIndex(0);
                                    ebo.addIndex(4);
                                    ebo.addIndex(7);
                                }
                                break;
                            case DOWN:
                                if (b.getPosition().y() != -(CUBE_SIZE - 1)) {
                                    ebo.addIndex(2);
                                    ebo.addIndex(1);
                                    ebo.addIndex(5);
                                    ebo.addIndex(6);
                                }
                                break;
                        }
                        break;
                    case 2:
                        switch (face) {
                            case FRONT:
                                if (b.getPosition().z() != CUBE_SIZE - 1) {
                                    ebo.addIndex(1);
                                    ebo.addIndex(0);
                                    ebo.addIndex(3);
                                    ebo.addIndex(2);
                                }
                                break;
                            case BACK:
                                if (b.getPosition().z() != -(CUBE_SIZE - 1)) {
                                    ebo.addIndex(5);
                                    ebo.addIndex(4);
                                    ebo.addIndex(7);
                                    ebo.addIndex(6);
                                }
                                break;
                        }
                        break;
                }
                ebo.end();
                vao.setIndexBuffer(ebo);
                shader.uniform("col", 0, 0, 0);
                glDrawElements(GL_TRIANGLE_FAN, ebo.getSize(), GL_UNSIGNED_INT, 0);
                ebo.clear();
            }

            float[] col = ColorUtil.hexToRGB(b.getFaces()[face]);
            ebo.bind();
            shader.uniform("col", col[0], col[1], col[2]);
            switch (face) {
                case FRONT:
                    if (b.getPosition().z() == CUBE_SIZE - 1) {
                        ebo.addIndex(1);
                        ebo.addIndex(0);
                        ebo.addIndex(3);
                        ebo.addIndex(2);
                    }
                    break;
                case UP:
                    if (b.getPosition().y() == CUBE_SIZE - 1) {
                        ebo.addIndex(3);
                        ebo.addIndex(0);
                        ebo.addIndex(4);
                        ebo.addIndex(7);
                    }
                    break;
                case RIGHT:
                    if (b.getPosition().x() == CUBE_SIZE - 1) {
                        ebo.addIndex(1);
                        ebo.addIndex(0);
                        ebo.addIndex(4);
                        ebo.addIndex(5);
                    }
                    break;
                case BACK:
                    if (b.getPosition().z() == -(CUBE_SIZE - 1)) {
                        ebo.addIndex(5);
                        ebo.addIndex(4);
                        ebo.addIndex(7);
                        ebo.addIndex(6);
                    }
                    break;
                case DOWN:
                    if (b.getPosition().y() == -(CUBE_SIZE - 1)) {
                        ebo.addIndex(2);
                        ebo.addIndex(1);
                        ebo.addIndex(5);
                        ebo.addIndex(6);
                    }
                    break;
                case LEFT:
                    if (b.getPosition().x() == -(CUBE_SIZE - 1)) {
                        ebo.addIndex(3);
                        ebo.addIndex(2);
                        ebo.addIndex(6);
                        ebo.addIndex(7);
                    }
                    break;
            }
            ebo.end();
            vao.setIndexBuffer(ebo);

            glDrawElements(GL_TRIANGLE_FAN, ebo.getSize(), GL_UNSIGNED_INT, 0);

            model.translate(new Vector3f(b.getPosition()).mul(-1));
            model.scale(1.005f);
            model.translate(new Vector3f(b.getPosition()));
            shader.uniform("model", model);

            shader.uniform("col", 0, 0, 0);
            glDrawElements(GL_LINE_LOOP, ebo.getSize(), GL_UNSIGNED_INT, 0);

            ebo.clear();

        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
