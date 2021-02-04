#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec4 color;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform vec3 col;

out vec4 faceColor;

void main() {
    gl_Position = projection * view * model * pos;
    faceColor = vec4(col, 1);
}
