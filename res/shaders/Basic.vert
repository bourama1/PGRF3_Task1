#version 330

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform mat4 u_Model;

uniform int u_Function;
uniform vec3 u_LightSource;

out vec2 texCoords;
out vec3 viewVec;
out vec3 lightVec;

const float delta = 0.001f;

// Object functions
// Kartezske sour.
vec3 funcDonut(vec2 inPos) {
    inPos.x *= 6.3f;
    inPos.y *= 6.3f;
    texCoords = mod(texCoords * vec2(2.0, 3.0), vec2(1.0, 1.0));
    float x = cos(inPos.x) * (3 + cos(inPos.y));
    float y = sin(inPos.x) * (3 + cos(inPos.y));
    float z = sin(inPos.y);
    return vec3(x, y, z);
}

vec3 funcFlower(vec2 inPos) {
    float x = inPos.x * 2 - 1;
    float y = inPos.y * 2 - 1;
    float z = 0.5f * cos(sqrt(20.f * x * x + 20.f * y * y));
    return vec3(x, y, z);
}

vec3 paramPos(vec2 inPosition){
    switch (u_Function) {
            case 0: return funcDonut(inPosition);
            case 1: return funcFlower(inPosition);
            default: return vec3(inPosition, 0.f);
    }
}

vec3 getNormal(vec2 inPos){
    vec3 tx = (paramPos(inPos + vec2(delta,0)) - paramPos(inPos - vec2(delta,0))) / vec3(1.f,1.f,2 * delta);
    vec3 ty = paramPos(inPos + vec2(0,delta)) - paramPos(inPos - vec2(0,delta)) / vec3(1.f,1.f,2 * delta);
    return cross(tx,ty);
}

mat3 getTBN(vec2 inPos, vec3 normal) {
    vec3 vTan = (paramPos(inPos + vec2(delta,0)) - paramPos(inPos - vec2(delta,0))) / vec3(1.f,1.f,2 * delta);
    vTan = normalize(vTan);
    vec3 vBi = cross(normal,vTan);
    vTan = cross(vBi,normal);
    return mat3(vTan,vBi,normal);
}


void main() {
    texCoords = inPosition;
    vec4 objectPosition = u_View * u_Model * vec4(paramPos(inPosition), 1.f);

    //Light
    vec3 viewDirection = normalize(- objectPosition.xyz);
    vec4 lightPosition = u_View * vec4(u_LightSource, 1.);
    vec3 toLightVector = normalize(lightPosition.xyz - objectPosition.xyz);

    //TBN
    vec3 normal = getNormal(inPosition);
    normal = normalize(inverse(transpose(mat3(u_View * u_Model))) * normal);
    mat3 tbn = getTBN(inPosition, normal);

    viewVec = transpose(tbn) * viewDirection;
    lightVec = transpose(tbn) * toLightVector;

    gl_Position = u_Proj * objectPosition;
}