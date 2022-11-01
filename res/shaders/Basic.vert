#version 330

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec3 normalVector;
out vec3 toLightVector;

vec3 lightSource = vec3(0.5,0.5,0.2);

vec3 getNormal() {
    //implementation
    return vec3(0.f,0.f,1.f);
}

void main() {
    //vec2 pos = inPosition * 2 - 1;
    //float z = 0.5 * cos(sqrt(20.f * pow(pos.x, 2.f) + 20.f * pow(pos.y, 2.f)));

    vec4 objectPosition = u_View * vec4(inPosition, 0.f, 1.f);

    //Phong ligth
    vec4 lightPosition = u_View * vec4(lightSource, 1.f);
    normalVector = transpose(inverse(mat3(u_View))) * getNormal();
    toLightVector = lightPosition.xyz - objectPosition.xyz;

    gl_Position = u_Proj * objectPosition;
}