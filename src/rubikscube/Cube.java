package rubikscube;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static java.lang.Math.PI;

public class Cube {

    public static float len = 2f;
    private Block[][][] blocks;
    private int size;
    private int rotationAxis;
    private int rotationColumn;
    private boolean rotationClockwise;
    private boolean rotating;
    private float offset;

    static Cube reference = new Cube(3);

    public Cube(int size) {
        this.size = size;
        blocks = new Block[size][size][size];
        offset = (size - 1) * len * .5f;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    float x = len * i - offset,
                            y = len * j - offset,
                            z = len * k - offset;
                    if (x == -(size - 1) || y == -(size - 1) || z == -(size - 1) || x == size - 1 || y == size - 1 || z == size - 1) {
                        blocks[i][j][k] = new Block(new Vector3f(x, y, z));
                        //System.out.println("x: " + x + ", y: " + y + ", z: " + z);
                    }
                }
            }
        }
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int[] pos) {
        Vector3f vector = new Vector3f(len * pos[0] - offset, len * pos[1] - offset, len * pos[2] - offset);
        //System.out.println("Vector: x: " + vector.x() + ", y: " + vector.y() + ", z: " + vector.z());
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    Block b = blocks[i][j][k];
                    if (b != null) {
                        if (b.getPosition().equals(vector)) {
                            return b;
                        }
                    }
                }
            }
        }
        //System.out.println("test");
        return null;
    }

    public int getSize() {
        return size;
    }

    public void rotate(boolean clockwise) {
        Matrix4f rotation = new Matrix4f();
        float angle = (float) PI / 2;
        if (!clockwise) {
            angle = -angle;
        }
        switch (getRotationAxis()) {
            case 0:
                rotation.rotate(angle, 1, 0, 0);
                break;
            case 1:
                rotation.rotate(angle, 0, 1, 0);
                break;
            case 2:
                rotation.rotate(angle, 0, 0, 1);
                break;
        }
        //float offset = (size - 1) * len * .5f;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    Block b = getBlocks()[x][y][z];
                    if (b != null) {
                        switch (getRotationAxis()) {
                            case 0:
                                if (b.getPosition().x() == getRotationColumn() * len - offset) {
                                    b.rotate(getRotationAxis(), clockwise);
                                    b.setPosition(b.getPosition().mulPosition(rotation));
                                }
                                break;
                            case 1:
                                if (b.getPosition().y() == getRotationColumn() * len - offset) {
                                    b.rotate(getRotationAxis(), clockwise);
                                    b.setPosition(b.getPosition().mulPosition(rotation));
                                }
                                break;
                            case 2:
                                if (b.getPosition().z() == getRotationColumn() * len - offset) {
                                    b.rotate(getRotationAxis(), clockwise);
                                    b.setPosition(b.getPosition().mulPosition(rotation));
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    public int getRotationAxis() {
        return rotationAxis;
    }

    public void setRotationAxis(int rotationAxis) {
        this.rotationAxis = rotationAxis;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }

    public int getRotationColumn() {
        return rotationColumn;
    }

    public void setRotationColumn(int rotationColumn) {
        this.rotationColumn = rotationColumn;
    }

    public boolean getClockwise() {
        return rotationClockwise;
    }

    public void setClockwise(boolean direction) {
        this.rotationClockwise = direction;
    }

    public boolean isSolved() {

        return false;
    }
}
