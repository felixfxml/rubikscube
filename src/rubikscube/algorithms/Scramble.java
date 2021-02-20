package rubikscube.algorithms;

import rubikscube.Cube;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Scramble {

    private Queue<Runnable> steps;
    private int stepCount;
    private Random random;
    private Cube cube;

    private FileWriter writer;
    public Scramble(int stepCount, Cube c, FileWriter writer) {
        this.writer = writer;
        this.cube = c;
        this.stepCount = stepCount;
        steps = new LinkedList<>();
        random = new Random();
        try {
            this.writer.write("scramble:\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < stepCount; i++) {
            rotateFaceCount(random.nextInt(6), random.nextInt(cube.getSize()), random.nextBoolean(), random.nextInt(2) + 1);
        }
        try {
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotateFaceCount(int face, int layer, boolean clockwise, int count) {
        count %= 4;
        if (count == 3) {
            count = 1;
            clockwise = !clockwise;
        }

        for (int i = 0; i < count; i++) {
            rotate(face % 3, face >= 3 ? layer : cube.getSize() - layer - 1, (face < 3) == clockwise);
        }
        try {
            writer.write("face: " + face + ", layer: " + layer + ", clockwise: " + clockwise + ", count: " + count + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotate(int axis, int layer, boolean clockwise) {
        steps.add(() -> {
            cube.setRotationAxis(axis);
            cube.setRotationColumn(layer);
            cube.setClockwise(clockwise);
            cube.setRotating(true);
        });
    }

    public boolean next() {
        if (!steps.isEmpty()) {
            steps.poll().run();
            return true;
        }
        return false;
    }
}
