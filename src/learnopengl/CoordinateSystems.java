package learnopengl;

import org.joml.Matrix4f;

public class CoordinateSystems {

    //OpenGL expects all coordinates to be normalized when running shaders (range from -1 to 1) -> called normalized device coordinates (NDC)
    //NDC are passed to the rasterizer which transforms them to 2D coordinates/pixels that are shown on the screen

    //transforming vertices to NDC is done by several steps
    //vertices are transformed in several coordinate systems
    //5 most important coordinate systems:

    //local space (object space)
    //world space
    //view space (eye space)
    //clip space
    //screen space

    //The Global Picture
    //transforming vertices using model, view and projection matrices
    //process can be represented like this: https://i.imgur.com/wgizW4e.png

    //1. local coordinates are relative to the object's local origin
    //2. transform local coordinates to world-space coordinates which are relative to an global origin
    //3. transform the world coordinates to view-space coordinates so they're as seen from the camera
    //4. project coordinates to clip coordinates
    //these are processed to NDC
    //5. transform clip coordinates to screen coordinates using glViewport

    //Local Space:
    //coordinate space where objects begin
    //for example each block of the rubik's cube has it's local origin a (0,0,0)

    //World Space:
    //if we used the local coordinates to render the blocks they would be all rendered at the same spot, so every block gets its own global coordinates, for example (1,0,-1)
    //accomplished by the model matrix

    //View Space:
    //also referred to as the camera
    //transforms world-space coordinates to coordinates that are in front of the user's view
    //accomplished by the view matrix

    //Clip Space:
    //OpenGL expects NDC, every coordinate outside of this range is clipped, the remaining coordinates will end up as fragments
    //view to clip-space transformation is done by a projection matrix which has a specific range
    //this viewing box is called a frustum
    //process of converting coordinates to NDC is called projection (3D to 2D)
    //perspective division: divide x,y,z by w (4D coordinates to 3D NDC)
    //is performed automatically at the end of the vertex shader
    //2 forms of projection matrices: orthographic and perspective

    //Orthographic projection:
    //cube-like frustum box
    //frustum defines the visible coordinates and is specified by a width, height, a near and a far plane
    //everything in front of the near plane and behind the far plane is clipped
    //directly maps all coordinates to NDC and won't touch the w coordinate

    private Matrix4f orthographic = new Matrix4f().ortho(0, 800, 0, 600, .1f, 100.0f);

    //no perspective -> unrealistic results

    //Perspective projection:
    //farther away objects appear smaller
    //projection matrix maps a given frustum range to clip space, but also manipulates the w value of each vertex, so that further away vertices get higher w values
    //after transforming each coordinate has a range from -w to w
    //perspective division

    //math behind the matrices: http://www.songho.ca/opengl/gl_projectionmatrix.html

    private Matrix4f perspective = new Matrix4f().perspective((float) java.lang.Math.toRadians(45), 800f / 600f, 0.1f, 100.0f);

    //creates a large frustum for the visible space
    //can be visualized as a non-uniformly shaped box (https://i.imgur.com/k8No4WP.png)
    //first parameter: FOV - field of view
    //second parameter: aspect ratio (for example 4:3 or 16:9)
    //third and fourth parameter: near and far plane

    //Everything together:
    //V_clip = M_projection * M_view * M_model * V_local

    //unlike the coordinate system we use in school the one in OpenGL has the y-axis going upwards and the z-axis coming forwards


}
