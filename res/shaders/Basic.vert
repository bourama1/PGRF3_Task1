#version 330

in vec2 inPos;

uniform mat4 u_View;
uniform mat4 u_Proj;

void main() {
    vec2 pos = inPos * 2 - 1;
    float z = 1/2 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2)));
    vec4 postPos = u_View * u_Proj * vec4(pos,z,1.f);
    gl_Position = postPos;
}