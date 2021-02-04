#version 330 core

out vec4 fragColor;
in vec4 faceColor;

void main() {
    fragColor = faceColor;
}
