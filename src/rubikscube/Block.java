package rubikscube;

import org.joml.Vector3f;

import java.util.ArrayList;

public class Block {

    public static final int RIGHT = 0, UP = 1, FRONT = 2, LEFT = 3, DOWN = 4, BACK = 5;
    public static final int[] color = new int[]{0x0046ad, 0xffffff, 0xb71234, 0x009b48, 0xffd500, 0xff5600};
    private Vector3f position;
    private int[] faces;

    public Block(Vector3f position) {
        this.position = position;
        faces = new int[6];
        System.arraycopy(color, 0, faces, 0, 6);
    }

    public int[] getFaces() {
        return faces;
    }

    public int[] getFacing() {
        ArrayList<Integer> ret = new ArrayList<>(3);
        float k = Math.max(Math.abs(position.x()), Math.max(Math.abs(position.y()), Math.abs(position.z())));
        if (k == Math.abs(position.x())) {
            ret.add(position.x() < 0 ? 3 : 0);
        }
        if (k == Math.abs(position.y())) {
            ret.add(position.y() < 0 ? 4 : 1);
        }
        if (k == Math.abs(position.z())) {
            ret.add(position.z() < 0 ? 5 : 2);
        }
        int[] ret2 = new int[3];
        for (int i = 0; i < 3; i++) {
            if (i < ret.size()) {
                ret2[i] = ret.get(i);
            } else {
                ret2[i] = -1;
            }
        }
        return ret2;
    }

    public int[] getPseudoPosition() {
        int[] ret = new int[3];
        float k = Math.max(Math.abs(position.x()), Math.max(Math.abs(position.y()), Math.abs(position.z())));
        ret[0] = (int) ((position.x() + k) / Cube.len);
        ret[1] = (int) ((position.y() + k) / Cube.len);
        ret[2] = (int) ((position.z() + k) / Cube.len);
        return ret;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    //"dreht" die farben wie bei einer 90° drehung
    public void rotate(int axis, boolean clockwise) {
        if (clockwise) {
            for (int i = 0; i < 3; i++) {
                rotate(axis, false);
            }
            return;
        }
        int[] newFaces = faces.clone();
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
}
