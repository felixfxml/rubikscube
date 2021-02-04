#version 330 core

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertColor;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec4 color;

void main() {
    gl_Position = projection * view * model * vec4(vertPos, 1.0);
    color = vec4(vertColor, 1.0);
}