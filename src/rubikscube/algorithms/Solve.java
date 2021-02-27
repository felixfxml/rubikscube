package rubikscube.algorithms;

import rubikscube.Block;
import rubikscube.Cube;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static rubikscube.Block.*;
import static rubikscube.Block.color;

public class Solve extends Thread {
    int moveCount = 0;
    final Object lock;

    public Queue<Runnable> steps = new LinkedList<>();
    private Cube cube;

    private FileWriter writer;
    public Solve(Cube cube, Object lock, FileWriter writer) {
        this.writer = writer;
        this.cube = cube;
        this.lock = lock;
    }

    public void run() {
        write("solve:\n");
        if (cube.isSolved()) {
            System.out.println("cube is already solved");
            return;
        }
        //getting colors
        int[] col = color.clone();
        if (cube.getSize() % 2 == 1) {
            int[] pos = new int[3];
            for (int i = 0; i < 6; i++) {
                pos[i % 3] = i < 3 ? cube.getSize() - 1 : 0;
                pos[(i + 1) % 3] = (cube.getSize() - 1) / 2;
                pos[(i + 2) % 3] = (cube.getSize() - 1) / 2;
                col[i] = cube.getBlock(pos).getFaces()[i];
            }
        }

        //solving centers & edges todo
        if (cube.getSize() > 3 && false) {
            //solving centers
            for (int face_1 = 0; face_1 < 1; face_1++) {
                int[] pos_1 = new int[3];
                pos_1[face_1 % 3] = face_1 < 3 ? cube.getSize() - 1 : 0;

                for (int x = 1; x < cube.getSize() - 1; x++) {
                    pos_1[(face_1 + 1) % 3] = x;
                    for (int y = 1; y < cube.getSize() - 1; y++) {
                        pos_1[(face_1 + 2) % 3] = y;
                        Block block_1 = cube.getBlock(pos_1);
                        if (block_1.getFaces()[face_1] == col[0]) {
                            continue; //skip if block already has color
                        }

                        // get matching block
                        Block block_2 = null;
                        int[] pos_2 = new int[3];
                        for (int i = 4; i < 24; i++) {
                            pos_2[0] = (i % 8 < 4 ? 0 : cube.getSize() - 1) + (i % 8 < 4 ? 1 : -1) * pos_1[(i-i%8)/8];
                            pos_2[1] = (i % 4 == 0 || i % 4 == 3 ? 0 : cube.getSize() -1) + (i % 4 == 0 || i % 4 == 3 ? 1 : -1) * (i % 8 > 1 && i % 8 < 6 ? pos_1[(( i - i % 8) / 8 + 2) % 3] : pos_1[(( i - i % 8) / 8 + 1) % 3]);
                            pos_2[2] = (i % 2 == 0 ? 0 : cube.getSize() -1) + (i % 2 == 0 ? 1 : -1) * (i % 8 > 1 && i % 8 < 6 ? pos_1[(( i - i % 8) / 8 + 1) % 3] : pos_1[(( i - i % 8) / 8 + 2) % 3]);
                            block_2 = cube.getBlock(pos_2);
                            if (block_2.getFaces()[block_2.getFacing()[0]] == col[0]) {
                                break;
                            }
                        }
                        int face_2 = block_2.getFacing()[0];

                        int k_1 = (face_1 + 1) % 3 == face_2 % 3 ? (face_1 + 2) % 3 : (face_1 + 1) % 3;
                        int k_2 = (face_1 + 1) % 3 == face_2 % 3 ? (face_1 + 1) % 3 : (face_1 + 2) % 3;

                        //solve part
                        if (x == (cube.getSize() - 1) / 2 || y == (cube.getSize() - 1) / 2) {//block is part of cross
                            boolean bool_1 = false;
                            if (face_1 % 3 == face_2 % 3) {
                                if (pos_1[k_1] == (cube.getSize() - 1) / 2) {
                                    int k_3 = k_1;
                                    k_1 = k_2;
                                    k_2 = k_3;
                                }
                            } else {
                                if (pos_1[k_1] == (cube.getSize() - 1) / 2) {
                                    rotateFaceCount(face_1, 0, true, 1);
                                    bool_1 = true;
                                    pos_1 = block_1.getPseudoPosition();
                                }
                            }
                            while (pos_1[k_1] != block_2.getPseudoPosition()[k_1]) {
                                rotateFaceCount(face_2, 0, true, 1);
                            }

                            int j = 0;
                            while (block_2.getFacing()[0] != face_1) {
                                this.rotateAxisCount(k_1, pos_1[k_1], true, 1);
                                j++;
                            }

                            rotateFaceCount(face_1, 0, true, 1);

                            this.rotateAxisCount(k_1, pos_1[k_1], false, j);
                            this.rotateAxisCount(k_1, cube.getSize() - 1 - pos_1[k_1], false, j);

                            rotateFaceCount(face_2, 0, true, 1);

                            this.rotateAxisCount(k_1, pos_1[k_1], false, j);

                            rotateFaceCount(face_2, 0, false, 1);

                            this.rotateAxisCount(k_1, pos_1[k_1], true, j);
                            this.rotateAxisCount(k_1, cube.getSize() - 1 - pos_1[k_1], true, j);

                            rotateFaceCount(face_1, 0, false, bool_1 ? 2 : 1);
                        }
                        /*
                        else if (x == y || x == cube.getSize() - 1 - y) {
                            if (face_2 % 3 == face_1 % 3) {
                                while (!(pos_1[(face_1 + 1) % 3] == block_2.getPseudoPosition()[(face_1 + 1) % 3] && pos_1[(face_1 + 2) % 3] == block_2.getPseudoPosition()[(face_1 + 2) % 3])) {
                                    rotate(face_2, 0, true);
                                }
                            } else {
                                while (!(pos_1[k_1] == block_2.getPseudoPosition()[k_1] &&
                                        (face_1 == (block_2.getPseudoPosition()[face_1 % 3] > (cube.getSize() - 1) / 2 ? face_1 % 3 : face_1 % 3 + 3)) ==
                                        (face_2 == (pos_1[(face_1 + k_2) % 3] > (cube.getSize() - 1) / 2 ? (face_1 + k_2) % 3 : (face_1 + k_2) % 3 +3)))) {
                                    rotate(face_2, 0, true);
                                }
                            }
                            int j = 0;
                            while (!(block_1.getFacing()[0] == face_2)) {
                                rotate_v2(k_1, pos_1[k_1], true);
                                j++;
                            }
                            boolean bool2 = true;
                            rotate(face_2, 0, true);
                            if (block_1.getPseudoPosition()[k_1] != pos_1[k_1]) {
                                rotate(face_2, 0, true);
                                rotate(face_2, 0, true);
                                bool2 = false;
                            }

                            for (int i = 0; i < j; i++) {
                                rotate_v2(k_1, pos_1[k_1], false);
                            }
                            rotate(face_2, 0, bool2);

                            for (int i = 0; i < j; i++) {
                                rotate_v2(k_1, pos_1[k_1], true);
                            }
                            rotate(face_2, 0, true);
                            rotate(face_2, 0, true);
                            for (int i = 0; i < j; i++) {
                                rotate_v2(k_1, pos_1[k_1], false);
                            }
                        }
                        */
                        else {//block is not part of cross

                        }
                    }
                }
            }
        }
        write("centers & edges solved\n");

        //solve like 3x3
        //solve down cross
        if (cube.getSize() > 2) {
            for (int face = 0; face < 6; face++) {
                if (face % 3 == 1) {
                    face++;
                }

                Block block = getEdge(col[DOWN], col[face]);
                int face_2 = block.getFacing()[0] % 3 == 1 ? block.getFacing()[1] : block.getFacing()[0];

                int j = 0;
                while (block.getPseudoPosition()[1] != cube.getSize() - 1) {
                    rotateFaceCount(face_2, 0, true, 1);
                    j++;
                }
                int k = 0;
                while (block.getFacing()[0] != face && block.getFacing()[1] != face) {
                    rotateFaceCount(UP, 0, true, 1);
                    k++;
                }
                if (k != 0) rotateFaceCount(face_2, 0, false, j);
                if (block.getFaces()[UP] != col[DOWN]) {
                    rotateFaceCount(face, 0, true, 1);
                    rotateFaceCount(DOWN, 0, true, 1);
                    rotateFaceCount(block.getFacing()[0] == face ? block.getFacing()[1] : block.getFacing()[0], 0, false, 1);
                    rotateFaceCount(DOWN, 0, false, 1);
                } else {
                    rotateFaceCount(face, 0, true, 2);
                }
            }
        }
        write("down cross solved\n");

        //solve down corners
        for (int i = 0; i < 4; i++) {
            //get Corner
            int x = i % 2 == 0 ? 0 : 3, z = i < 2 ? 2 : 5;
            Block block = getCorner(col[DOWN], col[x], col[z]);
            if (block.getPseudoPosition()[1] == 0) {
                int face_1 = block.getPseudoPosition()[0] == 0 ? 3 : 0;
                rotateFaceCount(face_1, 0, true, 1);
                boolean bool = false;
                if (block.getPseudoPosition()[1] == 0) {
                    rotateFaceCount(face_1, 0, true, 2);
                    bool = true;
                }
                rotateFaceCount(UP, 0, !bool, 1);
                rotateFaceCount(face_1, 0, bool, 1);
            }
            while (!(block.getFacing()[0] == x && block.getFacing()[1] == UP && block.getFacing()[2] == z)) {
                rotateFaceCount(UP, 0, true, 1);
            }
            boolean bool = true;
            while (true) {
                int face_1 = block.getPseudoPosition()[0] == 0 ? 3 : 0;
                rotateFaceCount(face_1, 0, bool, 1);
                if (block.getPseudoPosition()[1] == 0) {
                    rotateFaceCount(face_1, 0, true, 2);
                    bool = false;
                }
                rotateFaceCount(UP, 0, bool, 1);
                rotateFaceCount(face_1,0, !bool, 1);
                rotateFaceCount(UP, 0, !bool, 1);
                if (block.getFaces()[DOWN] != col[DOWN]) {
                    rotateFaceCount(face_1, 0, bool, 1);
                    rotateFaceCount(UP, 0, bool, 1);
                    rotateFaceCount(face_1,0, !bool, 1);
                    rotateFaceCount(UP, 0, !bool, 1);
                } else {
                    break;
                }
            }
        }
        write("down corners solved\n");

        //solve middle layer edges
        if (cube.getSize() > 2) {
            for (int i = 0; i < 4; i++) {
                int x = i % 2 == 0 ? 0 : 3, z = i < 2 ? 2 : 5;
                Block block = getEdge(col[x], col[z]);
                if (block.getPseudoPosition()[1] != cube.getSize() - 1) {
                    int face_1 = block.getPseudoPosition()[0] == 0 ? 3 : 0;
                    int face_2 = block.getPseudoPosition()[2] == 0 ? 5 : 2;
                    rotateFaceCount(face_1, 0, true, 1);
                    boolean bool = false;
                    if (block.getPseudoPosition()[1] == 0) {
                        rotateFaceCount(face_1, 0, true, 2);
                        bool = true;
                    }
                    rotateFaceCount(UP, 0, !bool, 1);
                    rotateFaceCount(face_1, 0, bool, 1);
                    rotateFaceCount(UP, 0, bool, 1);
                    rotateFaceCount(face_2, 0, bool, 1);
                    rotateFaceCount(UP, 0, bool, 1);
                    rotateFaceCount(face_2, 0, !bool, 1);
                }
                while (col[((block.getFacing()[0] == UP ? block.getFacing()[1] : block.getFacing()[0]) + 3) % 6] != block.getFaces()[UP]) {
                    rotateFaceCount(UP, 0, true, 1);
                }

                int face_1 = block.getPseudoPosition()[0] == 0 || block.getPseudoPosition()[0] == cube.getSize() - 1 ? x : z;
                int face_2 = face_1 == x ? z : x;
                boolean bool = true;
                rotateFaceCount(face_1, 0, true, 1);
                if (getCorner(col[DOWN], col[x], col[z]).getPseudoPosition()[1] == 0) {
                    rotateFaceCount(face_1, 0, true, 2);
                    bool = false;
                }
                rotateFaceCount(UP, 0, bool, 1);
                rotateFaceCount(face_1, 0, !bool, 1);
                rotateFaceCount(UP, 0, !bool, 1);
                rotateFaceCount(face_2, 0, !bool, 1);
                rotateFaceCount(UP, 0, !bool, 1);
                rotateFaceCount(face_2, 0, bool, 1);
            }
        }
        write("middle layer edges solved\n");

        //solve edges #2 todo
        if (cube.getSize() % 2 == 0 && cube.getSize() > 2) {}
        write("edges #2 solved\n");

        //solve up cross
        if (cube.getSize() > 2) {
            boolean bool = false;
            if (cube.getBlock(new int[]{0, cube.getSize() - 1, 1}).getFaces()[UP] != col[UP] && cube.getBlock(new int[]{cube.getSize() - 1, cube.getSize() - 1, 1}).getFaces()[UP] != col[UP]) {
                rotateFaceCount(UP, 0, true, 1);
                bool = true;
            } else if (cube.getBlock(new int[]{1, cube.getSize() - 1, 0}).getFaces()[UP] != col[UP] && cube.getBlock(new int[]{1, cube.getSize() - 1, cube.getSize() - 1}).getFaces()[UP] != col[UP]) {
                bool = true;
            }
            if (bool) {
                rotateFaceCount(FRONT, 0, true, 1);
                rotateFaceCount(RIGHT, 0, true, 1);
                rotateFaceCount(UP, 0, true, 1);
                rotateFaceCount(RIGHT, 0, false, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(FRONT, 0, false, 1);
            }
            if (cube.getBlock(new int[]{0, cube.getSize() - 1, 1}).getFaces()[UP] != col[UP] || cube.getBlock(new int[]{cube.getSize() - 1, cube.getSize() - 1, 1}).getFaces()[UP] != col[UP]) {
                while (!(cube.getBlock(new int[]{1, cube.getSize() - 1, cube.getSize() - 1}).getFaces()[UP] == col[UP] && cube.getBlock(new int[]{cube.getSize() - 1, cube.getSize() - 1, 1}).getFaces()[UP] == col[UP])) {
                    rotateFaceCount(UP, 0, true, 1);
                }
                rotateFaceCount(BACK, 0, true, 1);
                rotateFaceCount(UP, 0, true, 1);
                rotateFaceCount(LEFT, 0, true, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(LEFT, 0, false, 1);
                rotateFaceCount(BACK, 0, false, 1);
            }

            while (getEdge(col[FRONT], col[UP]).getPseudoPosition()[2] != cube.getSize() - 1) {
                rotateFaceCount(UP, 0, true, 1);
            }
            bool = false;
            if (getEdge(col[BACK], col[UP]).getPseudoPosition()[2] == 0) {
                if (getEdge(col[LEFT], col[UP]).getPseudoPosition()[0] != 0) {
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(RIGHT, 0, false, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(UP, 0, true, 2);
                    rotateFaceCount(RIGHT, 0, false, 1);

                    rotateFaceCount(UP, 0, false, 1);

                    rotateFaceCount(FRONT, 0, true, 1);
                    rotateFaceCount(UP, 0, false, 2);
                    rotateFaceCount(FRONT, 0, false, 1);
                    rotateFaceCount(UP, 0, false, 1);
                    rotateFaceCount(FRONT, 0, true, 1);
                    rotateFaceCount(UP, 0, false, 1);
                    rotateFaceCount(FRONT, 0, false, 1);
                }
            } else if (getEdge(col[LEFT], col[UP]).getPseudoPosition()[0] == 0) {
                rotateFaceCount(UP, 0, false, 1);

                rotateFaceCount(LEFT, 0, true, 1);
                rotateFaceCount(UP, 0, false, 2);
                rotateFaceCount(LEFT, 0, false, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(LEFT, 0, true, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(LEFT, 0, false, 1);
            } else if (getEdge(col[RIGHT], col[UP]).getPseudoPosition()[0] == cube.getSize() - 1) {
                rotateFaceCount(UP, 0, false, 1);

                rotateFaceCount(FRONT, 0, true, 1);
                rotateFaceCount(UP, 0, false, 2);
                rotateFaceCount(FRONT, 0, false, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(FRONT, 0, true, 1);
                rotateFaceCount(UP, 0, false, 1);
                rotateFaceCount(FRONT, 0, false, 1);
            } else {
                while (getEdge(col[LEFT], col[UP]).getPseudoPosition()[0] != 0) {
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(RIGHT, 0, false, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(UP, 0, true, 2);
                    rotateFaceCount(RIGHT, 0, false, 1);
                }
            }
        }
        write("up cross solved\n");

        //solve up corners
        boolean bool = true;
        while (bool) {
            int k = 0;
            int i = 0;
            if (Arrays.equals(getCorner(col[UP], col[FRONT], col[LEFT]).getPseudoPosition(), new int[]{0, cube.getSize() - 1, cube.getSize() - 1})) {
                i++;
            }
            if (Arrays.equals(getCorner(col[UP], col[BACK], col[LEFT]).getPseudoPosition(), new int[]{0, cube.getSize() - 1, 0})) {
                i++;
                k = 1;
            }
            if (Arrays.equals(getCorner(col[UP], col[BACK], col[RIGHT]).getPseudoPosition(), new int[]{cube.getSize() - 1, cube.getSize() - 1, 0}))  {
                i++;
                k = 2;
            }
            if (Arrays.equals(getCorner(col[UP], col[FRONT], col[RIGHT]).getPseudoPosition(), new int[]{cube.getSize() - 1, cube.getSize() - 1, cube.getSize() - 1}))  {
                i++;
                k = 3;
            }
            switch (i) {
                case 2:
                    //todo
                case 4:
                    bool = false;
                    while (getEdge(col[LEFT], col[UP]).getPseudoPosition()[0] != 0) {
                        rotateFaceCount(UP, 0, true, 1);
                    }
                    break;
                case 1:
                case 0:
                    rotateFaceCount(UP, 0, false, k);
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(UP, 0, false, 1);
                    rotateFaceCount(LEFT, 0, false, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(RIGHT, 0, false, 1);
                    rotateFaceCount(UP, 0, false, 1);
                    rotateFaceCount(LEFT, 0, true, 1);
                    rotateFaceCount(UP, 0, true, 1);
                    rotateFaceCount(UP, 0, true, k);
                    break;
            }
        }
        for (int i = 0; i < 4; i++) {
            while(cube.getBlock(new int[]{cube.getSize() -1, cube.getSize() -1, cube.getSize() -1}).getFaces()[UP] != col[UP]) {
                for (int j = 0; j < 2; j++) {
                    rotateFaceCount(RIGHT, 0, true, 1);
                    rotateFaceCount(FRONT, 0, false, 1);
                    rotateFaceCount(RIGHT, 0, false, 1);
                    rotateFaceCount(FRONT, 0, true, 1);
                }
            }
            rotateFaceCount(UP, 0, true, 1);
        }
        write("solved");

        System.out.println("solved");
        System.out.println("move count: " + moveCount);

        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Block getEdge(int col1, int col2) {
        ArrayList<Integer> test = new ArrayList<>(2);
        test.add(col1);
        test.add(col2);
        for (int i = 0; i < 12; i++) {
            int[] pos = new int[3];
            int k = (i - i % 4) / 4;
            pos[k] = 1;
            pos[(k + 1) % 3] = i % 2 == 0 ? 0 : cube.getSize() - 1;
            pos[(k + 2) % 3] = i % 4 < 2 ? 0 : cube.getSize() - 1;
            Block block = cube.getBlock(pos);
            if (test.contains(block.getFaces()[block.getFacing()[0]]) && test.contains(block.getFaces()[block.getFacing()[1]])) {
                return block;
            }
        }
        return null;
    }

    private Block getCorner(int col1, int col2, int col3) {
        ArrayList<Integer> test = new ArrayList<>(3);
        test.add(col1);
        test.add(col2);
        test.add(col3);
        for (int i = 0; i < 8; i++) {
            int[] pos = new int[3];
            pos[0] = i < 4 ? 0 : cube.getSize() - 1;
            pos[1] = i % 2 == 0 ? 0 : cube.getSize() - 1;
            pos[2] = i % 4 < 2 ? 0 : cube.getSize() - 1;
            Block block = cube.getBlock(pos);
            if (test.contains(block.getFaces()[block.getFacing()[0]]) && test.contains(block.getFaces()[block.getFacing()[1]]) && test.contains(block.getFaces()[block.getFacing()[2]])) {
                return block;
            }
        }
        return null;
    }

    private void rotateFaceCount(int face, int layer, boolean clockwise, int count) {
        count %= 4;
        if (count == 3) {
            count = 1;
            clockwise = !clockwise;
        }

        for (int i = 0; i < count; i++) {
            rotate(face % 3, face >= 3 ? layer : cube.getSize() - layer - 1, (face < 3) != clockwise);
        }
        write("face: " + face + ", layer: " + layer + ", clockwise: " + clockwise + ", count: " + count + "\n");
    }

    private void rotateAxisCount(int axis, int layer, boolean clockwise, int count) {
        boolean bool = layer < (cube.getSize() / 2);
        rotateFaceCount(axis + (bool ? 3 : 0), bool ? cube.getSize() - 1 - layer : layer, bool != clockwise, count);
    }

    private void rotate(int axis, int layer, boolean clockwise) {
        steps.add(() -> {
            cube.setRotationAxis(axis);
            cube.setRotationColumn(layer);
            cube.setClockwise(clockwise);
            cube.setRotating(true);
        });
        moveCount++;
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean next() {
        if (!steps.isEmpty()) {
            steps.poll().run();
            return true;
        }
        return false;
    }
}
