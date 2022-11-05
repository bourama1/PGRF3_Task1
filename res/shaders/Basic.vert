#version 330

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;
out vec3 viewVec;
out vec3 lightVec;

vec3 lightSource = vec3(0.5,0.5,0.5);

vec3 getNormal(){
    float zu = 0.f;
    float zv = 0.f;
    vec3 u = vec3(1,0,zu);
    vec3 v = vec3(0,1,zv);
    return cross(u,v);
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
    vec3 normal = getNormal();
    normal = inverse(transpose(mat3(u_View)))*normal;

    vec3 tangent = mat3(u_View) * getTangent(normal);
    vec3 bitangent = cross(normalize(normal), normalize(tangent));
    tangent = cross(bitangent, normal);

    mat3 tbn = mat3(tangent, bitangent, normal);

    viewVec = transpose(tbn) * viewDirection;
    lightVec = transpose(tbn) * toLightVector;

    gl_Position = u_Proj * objectPosition;
}