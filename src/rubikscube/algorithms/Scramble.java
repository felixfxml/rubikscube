package rubikscube.algorithms;

import rubikscube.Cube;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Scramble {

    private Queue<Runnable> steps;
    private int stepCount;
    private Random random;

    public Scramble(int stepCount, Cube c) {
        this.stepCount = stepCount;
        steps = new LinkedList<>();
        random = new Random();
        for (int i = 0; i < stepCount; i++) {
            steps.add(() -> {
                c.setRotationAxis(random.nextInt(3));
                c.setRotationColumn(random.nextInt(c.getSize()));
                c.setRotating(true);
            });
        }
    }

    public void next() {
        if (!steps.isEmpty())
            steps.poll().run();
    }

}
