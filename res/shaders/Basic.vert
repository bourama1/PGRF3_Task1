#version 330

in vec2 inPos;
in vec3 inColor;

out vec3 vertColor;

void main() {
    vertColor = inColor;
    gl_Position = vec4(inPos, 0.0f, 1.0f);
}