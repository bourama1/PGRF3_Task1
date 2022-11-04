#version 330

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;
out vec3 viewVec;
out vec3 lightVec;

vec3 lightSource = vec3(0.5,0.5,0.5);

const float PI=3.1415926;

vec3 getNormal(float x, float y){
    float a = x*PI*2.;
    float z = y*PI - PI/2.;
    vec3 dx = vec3(-3*sin(a)*cos(z)*PI*2.,
                   2.*cos(a)*cos(z)*2.*PI,
                   0.);
    vec3 dy = vec3(-3.*cos(a)*sin(z)*PI,
                   -2.*sin(a)*sin(z)*PI,
                   cos(z)*PI);
    return cross(dx,dy);
}


vec3 getTangent(vec3 normal) {
    vec3 tangent;

    vec3 c1 = cross(normal, vec3(0.0, 0.0, 1.0));
    vec3 c2 = cross(normal, vec3(0.0, 1.0, 0.0));

    if(length(c1) > length(c2))
    {
        tangent = c1;
    }
    else
    {
        tangent = c2;
    }
    return tangent;
}


void main() {
    texCoords = inPosition;

    vec4 objectPosition = u_View * vec4(inPosition, 0.f, 1.f);
    vec3 viewDirection = - normalize(objectPosition.xyz);

    //Phong ligth
    vec4 lightPosition = u_View * vec4(lightSource, 1.);
    vec3 toLightVector = normalize(lightPosition.xyz - objectPosition.xyz);
    vec3 normal = getNormal(inPosition.x, inPosition.y);
    normal = inverse(transpose(mat3(u_View)))*normal;

    vec3 tangent = mat3(u_View) * getTangent(normal);
    vec3 bitangent = cross(normalize(normal), normalize(tangent));
    tangent = cross(bitangent, normal);

    mat3 tbn = mat3(tangent, bitangent, normal);

    viewVec = transpose(tbn) * viewDirection;
    lightVec = transpose(tbn) * toLightVector;

    gl_Position = u_Proj * objectPosition;
}