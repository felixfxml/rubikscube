package learnopengl;

public class Shaders {

    //written in GLSL (C-like)
    //begin with a version declaration (#version 330 core)
    //input and output variables
    //uniforms
    //main function

    //input variables in a vertex shader are also called 'vertex attributes'
    //hardware allows us to declare at least 16 4-component vertex attributes (vec1, ... vec4)

    //basic data types in GLSL:
    //int
    //float
    //double
    //uint
    //bool

    //int: holds values from -2^31 to 2^31-1 (positives and negatives)
    //uint: holds values from 0 to 2^32-1 (only positives and zero)

    //other data types:
    //Vertices
    //Matrices
    //Samplers

    //vectors: n-component container for any basic data types (n = 1 to 4)
    //vecn: vector of floats
    //bvecn ... booleans
    //ivecn ... ints
    //uvecn ... unsigned ints
    //dvecn ... doubles

    //access of the components:
    //xyzw for coordinates
    //rgba for colors
    //stpq for texture coordinates

    //vectors allow flexible component selection (swizzling) which allows syntax like
    //vec2 v2;
    //vec3 v3 = v2.yxx;
    //vec4 v4 = vec4(v2, v3.xz);

    //in and out variables
    //each shader can specify in and out variables
    //whenever an out name matches an in name of the next shader (vertex shader->tesselation->geometry shader->fragment shader) it is passed to the next shader

    //vertex shader gets input straight from the vertex data
    //layout specification required for inputs so it can be linked with the vertex data
    //fragment shader requires a vec4 color output

    //uniforms
    //pass data from application(CPU) to shader(GPU)
    //unlike vertex attribs, they're global -> unique per shader program object, can be accessed from any shader at any stage in the shader program
    //will keep their values until they're reset or updated

    //get uniform location via glGetUniformLocation(program, uniformName); (does not require the program to be used)
    //updating uniform values requires the program to be used (glUseProgram(program);)
    //set/update uniform value via glUniform4f(uniformID, value1, value2, value3, value4);

    //for gradient colors:
    //specify colors per vertex
    //change the VBO memory (glVertexAttribPointer)
    //add input variable in the vertex shader, pass it to the fragment shader
    //fragment interpolation will do its thing

}
