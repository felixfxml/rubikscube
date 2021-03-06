package rubikscube;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import rubikscube.algorithms.Scramble;
import rubikscube.algorithms.Solve;
import rubikscube.opengl.*;
import rubikscube.util.ColorUtil;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static rubikscube.Block.*;

public class Main {

    public static Object lock;
    public int CUBE_SIZE = 3;
    public float[] speed = new float[]{0.3f};
    int[] i = new int[]{0};
    private double rotationStart;
    private long window;
    private int width, height;
    private VertexArray vao;
    private ElementArrayBuffer ebo;
    private VertexBuffer vbo;
    private Cube cube;
    private Camera camera;
    private Shader shader;
    private float[] rotationSpeed = new float[]{5};
    private ImGuiImplGlfw imGuiGLFW = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGL3 = new ImGuiImplGl3();
    private int[] scrambleSteps = new int[]{100};
    private Scramble scramble;
    private Solve solve;
    private File log;
    private FileWriter writer;
    private boolean logs = false;
    private int[] cube_size_buffer = new int[]{3};
    private int steps = 0;
    private boolean advancedScramble = false;
    private boolean tutorial = false;
    //rotation
    private int face = 0;
    private int layer = 0;
    private boolean clockwise = true;
    private String[] faces = new String[]{"RIGHT", "UP", "FRONT", "LEFT", "DOWN", "BACK"};
    //lang
    private int lang = 1;
    private static final int EN = 0;
    private static final int DE = 1;
    private final String[][] langs = new String[][]{{"Rubik's Cube", "Cube", "Cube Size", "Rotation Speed", "Layer:", "Face:", "Clockwise:", "Go!", "Scramble", "Solve", "Cancel", "Advanced Scramble", "Scramble Step Count",
            "Solve Steps: ", "Your Steps: ", "Reset your steps", "Camera", "Camera Speed", "Create Logs", "Quit", "Move Notation: "},
            {"Zauberwürfel", "Würfel", "Würfelgröße", "Rotationsgeschwindigkeit", "Schicht:", "Seite:", "Uhrzeigersinn:", "Los!", "Verdrehen", "Lösen", "Abbrechen", "Erweitertes Verdrehen", "Schrittzahl",
                    "Lösungschritte: ", "Deine Schritte: ", "Deine Schritte Zurücksetzen", "Kamera", "Kamerageschwindigkeit", "Logs erstellen", "Beenden", "Bewegungsnotation"}};

    //Konstruktor für das Main Fenster und initialisiert nötige Variablen
    public Main(int width, int height) {
        setWidth(width);
        setHeight(height);
        cube = new Cube(CUBE_SIZE);
        camera = new Camera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        createLog();
        solve = new Solve(cube, lock, writer);
        scramble = new Scramble(0, cube, writer);
        lock = new Object();
    }

    //main Methode, initialisiert und startet Main;
    public static void main(String[] args) {
        new Main(800, 600).run();
    }

    //erstellt ein Log File, welches sämtliche Rotationen des Cubes speichert
    public void createLog() {
        File file = new File("rubikscube-logs");
        if (!file.exists()) {
            file.mkdir();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdir();
        }
        log = new File("rubikscube-logs" + File.separator + "log-" + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".txt");
        try {
            log.createNewFile();
            writer = new FileWriter(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        solve = new Solve(cube, lock, writer);
        solve.steps.clear();
        scramble = new Scramble(scrambleSteps[0], cube, writer);
        scramble.steps.clear();
    }

    //schließt das programm, wenn die escape taste gedrückt wird
    public void handleInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    //erstellt ein glfw fenster und führt die schleife aus, in der alle gerendert und berechnet wird
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

        ImGui.createContext();
        imGuiGLFW.init(window, false);
        imGuiGL3.init("#version 330 core");

        glClearColor(.15f, .15f, .2f, 1.0f);

        while (!glfwWindowShouldClose(window)) {

            handleInput();

            algorithms();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float distance = CUBE_SIZE * 5f;

            Vector3f camPos = new Vector3f((float) Math.sin(glfwGetTime() * speed[0]) * distance, distance * .5f, (float) Math.cos(glfwGetTime() * speed[0]) * distance);

            camera.setPosition(camPos);

            Matrix4f ortho = new Matrix4f().ortho(-CUBE_SIZE, CUBE_SIZE, -CUBE_SIZE, CUBE_SIZE, 0, CUBE_SIZE * CUBE_SIZE);
            Matrix4f projection = new Matrix4f().perspective(45, (float) getWidth() / getHeight(), .001f, distance * 10);
            Matrix4f view = camera.getView();

            vao.bind();
            shader.bind();
            shader.uniform("projection", projection);
            shader.uniform("view", view);

            renderCube();

            shader.unbind();
            vao.unbind();
            ebo.unbind();

            imGuiGLFW.newFrame();
            ImGui.newFrame();

            renderGui();

            ImGui.render();
            imGuiGL3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        if (!logs) log.delete();

        glfwTerminate();
        System.exit(0);

    }

    //führt entweder den lösungsalgorithmus oder den zum zufälligen drehen aus
    public void algorithms() {
        if (!cube.isRotating()) {
            if (!scramble.next()) {
                if (!solve.next()) {
                    if (solve.getState() == Thread.State.WAITING) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            }

            rotationStart = glfwGetTime();

        }
    }

    //rendert das gui
    public void renderGui() {
        int langbuffer = lang;
        ImGui.begin(langs[lang][0]);

        ImGui.text(langs[lang][1]);
        ImGui.sameLine();
        if (ImGui.button("Reset")) {
            scramble.steps.clear();
            solve.interrupt();
            solve.steps.clear();
            CUBE_SIZE = cube_size_buffer[0];
            cube = new Cube(CUBE_SIZE);
            steps = 0;
            if (!logs) log.delete();
            createLog();
        }
        ImGui.sameLine();
        if (ImGui.button(langbuffer == 0 ? "EN" : "DE")) {
            if (langbuffer == DE) {
                langbuffer = EN;
            } else if (langbuffer == EN) {
                langbuffer = DE;
            }
        }
        ImGui.sliderInt(langs[lang][2], cube_size_buffer, 2, 25);
        ImGui.sliderFloat(langs[lang][3], rotationSpeed, 0.1f, 20);
        ImGui.separator();
        ImGui.text("Rotation");

        ImGui.text(langs[lang][5]);
        ImGui.sameLine();
        if (ImGui.button(faces[face])) {
            if (face == 5) {
                face = 0;
            } else {
                face++;
            }
        }
        if (CUBE_SIZE > 3) {
            ImGui.text(langs[lang][4]);
            ImGui.sameLine();
            if (ImGui.button(String.valueOf(layer + 1))) {
                if (layer + 2 > cube.getSize() / 2) {
                    layer = 0;
                } else {
                    layer++;
                }
            }
        }
        ImGui.text(langs[lang][6]);
        ImGui.sameLine();
        if (ImGui.radioButton(String.valueOf(clockwise), clockwise)) {
            clockwise = !clockwise;
        }

        ImGui.text(langs[lang][20] + ((layer > 0) ? layer + 1 : "") + faces[face].charAt(0) + ((!clockwise) ? "'" : ""));
        ImGui.sameLine();
        if (ImGui.button("Notation help...")) {
            try {
                Desktop.getDesktop().browse(new URI("https://ruwix.com/the-rubiks-cube/notation/"));
                Desktop.getDesktop().browse(new URI("https://ruwix.com/the-rubiks-cube/notation/advanced/"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if (ImGui.button(langs[lang][7])) {
            rotate(face % 3, face >= 3 ? layer : cube.getSize() - layer - 1, (face < 3) != clockwise);
            write("face: " + face + ", layer: " + layer + ", clockwise: " + clockwise + ", count: " + 1 + "\n");
        }
        if (ImGui.button(langs[lang][8]) && !cube.isRotating()) {
            solve.interrupt();
            solve.steps.clear();
            scramble = new Scramble(scrambleSteps[0], cube, writer);
        }
        ImGui.sameLine();

        if (ImGui.button(langs[lang][9]) && !cube.isSolved() && CUBE_SIZE == 3) {
            scramble.steps.clear();
            solve = new Solve(cube, lock, writer);
            solve.start();
        }
        ImGui.sameLine();

        if (ImGui.button(langs[lang][10])) {
            scramble.steps.clear();
            solve.interrupt();
            solve.steps.clear();
        }

        ImGui.sameLine();

        if (ImGui.radioButton(langs[lang][11], advancedScramble)) {
            advancedScramble = !advancedScramble;
        }
        if (advancedScramble) {
            ImGui.sliderInt(langs[lang][12], scrambleSteps, 1, CUBE_SIZE * CUBE_SIZE * CUBE_SIZE * 10);
        }

        ImGui.text(langs[lang][13] + solve.moveCount);
        ImGui.sameLine();
        ImGui.text(langs[lang][14] + steps);

        if (ImGui.button(langs[lang][15])) {
            steps = 0;
        }

        ImGui.separator();

        ImGui.text(langs[lang][16]);

        ImGui.sliderFloat(langs[lang][17], speed, -3, 3);

        ImGui.separator();

        if (ImGui.radioButton(langs[lang][18], logs)) {
            logs = !logs;
        }

        ImGui.sameLine();

        if (ImGui.button(langs[lang][19])) {
            if (!logs) log.delete();

            glfwTerminate();
            System.exit(0);
        }

        if (lang == DE) {
            ImGui.separator();

            if (ImGui.button("Tutorial")) {
                tutorial = ! tutorial;
            }

            if (tutorial) {
                ImGui.text("Seite - wähle die Seite, die roitert wird, aus");
                ImGui.text("Schicht - wähle die Schicht, die rotiert wird aus \n (nur relevant bei größeren Würfeln; äußere Schicht = 1)");
                ImGui.text("Uhrzeigersinn - wähle die Richtung, in die rotiert wird, aus \n (ausgehend von der Seite)");
                ImGui.text("Hilfe zu den Bewegungsnotationen:");
                ImGui.text("3x3: https://ruwix.com/the-rubiks-cube/notation/");
                ImGui.sameLine();
                if (ImGui.button("Öffnen...")) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://ruwix.com/the-rubiks-cube/notation/"));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                ImGui.text("Große Würfel: https://ruwix.com/the-rubiks-cube/notation/advanced/");
                ImGui.sameLine();
                if (ImGui.button("Öffnen...")) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://ruwix.com/the-rubiks-cube/notation/advanced/"));;
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ImGui.end();

        lang = langbuffer;
    }

    //Führt vom Nutzer eingegebene Rotation aus
    private void rotate(int axis, int layer, boolean clockwise) {
        cube.setRotationAxis(axis);
        cube.setRotationColumn(layer);
        cube.setClockwise(clockwise);
        cube.setRotating(true);
        steps++;
    }

    //loggt Nutzer Rotation
    private void write(String s) {
        try {
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //initialisiert den einzelnen würfel
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

    //rendert alle würfel
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

    //rendert einen einzelnen würfel
    public void renderBlock(Block b) {
        for (int face = 0; face < b.getFaces().length; face++) {

            float offset = (CUBE_SIZE - 1) * Cube.len * .5f;
            Matrix4f model = new Matrix4f();
            if (cube.isRotating()) {
                float angle = (float) Math.toRadians((glfwGetTime() - rotationStart) * 90.0f) * rotationSpeed[0];
                switch (cube.getRotationAxis()) {
                    case 0:
                        if (b.getPosition().x() == cube.getRotationColumn() * Cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), cube.getClockwise() ? 1 : -1, 0, 0);
                        break;
                    case 1:
                        if (b.getPosition().y() == cube.getRotationColumn() * Cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), 0, cube.getClockwise() ? 1 : -1, 0);
                        break;
                    case 2:
                        if (b.getPosition().z() == cube.getRotationColumn() * Cube.len - offset)
                            model.rotate((float) Math.min(angle, Math.toRadians(90)), 0, 0, cube.getClockwise() ? 1 : -1);
                        break;
                }
                if (angle >= Math.toRadians(90)) {
                    cube.rotate(cube.getClockwise());
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

    //getter-Methode für Fensterbreite
    public int getWidth() {
        return width;
    }

    //setter-Methode für Fensterbreite
    public void setWidth(int width) {
        this.width = width;
    }

    //getter-Methode für Fensterhöhe
    public int getHeight() {
        return height;
    }

    //setter-Methode für Fensterhöhe
    public void setHeight(int height) {
        this.height = height;
    }
}
