#version 330

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

void main() {
    vec2 pos = inPosition * 2 - 1;
    float z = 0.5 * cos(sqrt(20.f * pow(pos.x, 2.f) + 20.f * pow(pos.y, 2.f)));
    vec4 posMVP = u_Proj * u_View * vec4(inPosition, z, 1.f);
    gl_Position = posMVP;

}