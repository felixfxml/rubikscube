package rubikscube;

import org.joml.Vector3f;

import java.util.Objects;

public class Block {

    public static final int FRONT = 0, UP = 1, RIGHT = 2, BACK = 3, DOWN = 4, LEFT = 5;
    private Vector3f position;
    private int[] faces;

    public Block(Vector3f position) {
        this.position = position;
        faces = new int[6];
        faces[FRONT] = 0x009b48;//green front
        faces[UP] = 0xffffff;//white up
        faces[RIGHT] = 0xb71234;//red right
        faces[BACK] = 0xffd500;//yellow back
        faces[DOWN] = 0x0046ad;//blue down
        faces[LEFT] = 0xff5600;//orange left
    }

    public Block clone() {
        return new Block(new Vector3f(getPosition()));
    }

    public int[] getFaces() {
        return faces;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void rotate(int axis, boolean clockwise) {
        int[] newFaces = faces.clone();
        if (clockwise) {
            for (int i = 0; i < 3; i++) {
                rotate(axis, false);
            }
            return;
        }
        switch (axis) {
            case 0://x-axis
                newFaces[UP] = faces[FRONT];
                newFaces[BACK] = faces[UP];
                newFaces[DOWN] = faces[BACK];
                newFaces[FRONT] = faces[DOWN];
                break;
            case 1://y-axis
                newFaces[FRONT] = faces[RIGHT];
                newFaces[LEFT] = faces[FRONT];
                newFaces[BACK] = faces[LEFT];
                newFaces[RIGHT] = faces[BACK];
                break;
            case 2://z-axis
                newFaces[UP] = faces[LEFT];
                newFaces[RIGHT] = faces[UP];
                newFaces[DOWN] = faces[RIGHT];
                newFaces[LEFT] = faces[DOWN];
                break;
        }
        faces = newFaces.clone();
    }

    public boolean equals(Block b) {
        return b.getPosition().equals(position);
    }

    public int getId() {
        return Objects.hash(getPosition().x(), getPosition().y(), getPosition().z());
    }
}
