#version 330

in vec2 inPos;
in vec3 inColor;

out vec4 o_Color;

void main() {
    gl_Position = vec4(inPos, 0.0f, 1.0f);
    o_Color = vec4(inColor, 1.0f);
}