package learnopengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    //OpenGL itself doesn't have a concept of a camera, so we have to simulate one by moving everything but ourselves

    //view matrix transforms all world coordinates to view coordinates that are relative to the camera's position and direction

    //define the camera position

    //define the direction
    //define the right axis (from camera's perspective)
    //define the up axis
    //this process is called the Gram-Schmidt process in linear algebra

    //Look At-Matrix
    //define a coordinate space with 3 non-linear or perpendicular axes (direction, right, up)
    //create a matrix with these 3 axes plus a translation vector
    //transform any vector to that coordinate space

    private Matrix4f lookAt = new Matrix4f().lookAt(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

    //first parameter: position
    //second parameter: target
    //third parameter: up-vector




}
