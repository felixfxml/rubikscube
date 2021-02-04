package rubikscube.opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Vector3f target;
    private Matrix4f view;

    public Camera(Vector3f position, Vector3f target) {
        this.position = position;
        this.target = target;
        update();
    }

    public void update() {
        view = new Matrix4f().lookAt(position, target, new Vector3f(0, 1, 0));
    }

    public Matrix4f getView() {
        return view;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        update();
    }

    public Vector3f getTarget() {
        return target;
    }

    public void setTarget(Vector3f target) {
        this.target = target;
    }
}
