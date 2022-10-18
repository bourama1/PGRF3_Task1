#version 330

in vec3 normalVector;
in vec3 toLightVector;

uniform float u_ColorR;

out vec4 outColor;

vec4 ambientColor = vec4(1.0, 0.f, 0.f, 1.f);
vec4 diffuseColor = vec4(0.9, 0.9, 0.9, 1.f);

void main() {
    vec4 baseColor = vec4(u_ColorR, 0.1f, 0.1f, 1.f);

    //Diffuse ligth
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normalVector);
    float NDotL = max(dot(nd, ld), 0.f);
    vec4 diffuse = NDotL * diffuseColor;

    outColor = diffuse * baseColor;
}
