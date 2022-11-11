#version 330
#define PI 3.1415926538

in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform mat4 u_Model;

uniform vec3 u_LightSource;

uniform int u_Function;
uniform int u_TimeRunning;
uniform int u_Grid;

uniform float u_Time;

out vec2 texCoords;
out vec2 texScale;
out vec3 viewVec;
out vec3 lightVec;

const float delta = 0.001f;
float scale = 1.0f;

// Object functions
// Kartezske sour.
vec3 objFlower(vec2 inPos) {
    texScale = vec2(2.f);
    float x = inPos.x * 2 - 1;
    float y = inPos.y * 2 - 1;
    float z = 0.5f * cos(sqrt(20.f * x * x + 20.f * y * y));

    if(u_TimeRunning == 1)
            return vec3(x, y, z) * u_Time;

    return vec3(x, y, z);
}

vec3 objDonut(vec2 inPos) {
    texScale = vec2(6.3f);
    inPos.x *= 6.3f;
    inPos.y *= 6.3f;
    float x = cos(inPos.x) * (3 + cos(inPos.y));
    float y = sin(inPos.x) * (3 + cos(inPos.y));
    float z = sin(inPos.y);

    if(u_TimeRunning == 1)
            return vec3(x, y, z) * u_Time;

    return vec3(x, y, z);
}

// Sfericke
vec3 objElephantHead(vec2 inPos) {
    texScale = vec2(PI);
    float zenit = inPos.x * PI;
    float azimut = inPos.y * 2.f * PI;
    float r = 3.f + cos(4.f * azimut);

    if(u_TimeRunning == 1)
            r *= u_Time;

    return vec3(
            sin(zenit) * cos(azimut) * r,
            sin(zenit) * sin(azimut) * r,
            cos(zenit) * r);
}

vec3 objShell(vec2 inPos) {
    texScale = vec2(PI);
    float zenit = inPos.x * PI;
    float azimut = inPos.y * 2.f * PI;
    float r = sin(zenit) * azimut;

    if(u_TimeRunning == 1)
            r *= u_Time;

    return vec3(
        sin(zenit) * cos(azimut) * r,
        sin(zenit) * sin(azimut) * r,
        cos(zenit) * r);
}

// Cylindricke
vec3 objSombrero(vec2 inPos) {
    texScale = vec2(PI);
    float r = inPos.x * 2.f * PI;
    if(u_TimeRunning == 1)
            r *= u_Time;

    float azimut = inPos.y * 2.f * PI;
    float v = 2.f * sin(r);

    return vec3(
            cos(azimut) * r,
            sin(azimut) * r,
            v
    );
}

vec3 objWineGlass(vec2 inPos){
    texScale = vec2(PI);
    float azimut = PI * 0.5 - PI * inPos.x * 2;
    float v = PI * 0.5 - PI * inPos.y * 2;
    float r = 1.f + cos(v);

    if(u_TimeRunning == 1)
            r *= u_Time;

    return vec3(
            cos(azimut) * r,
            sin(azimut) * r,
            v
    );
}

vec3 posCalc(vec2 inPosition){
    if(u_Grid == 1)
            return vec3(inPosition, 0.f);
    else if (u_Grid == 2)
            return vec3(u_LightSource.x + ((inPosition.x - 0.5f) / 4), u_LightSource.y + ((inPosition.y - 0.5f) / 4), u_LightSource.z);
    switch (u_Function) {
            case 1: return objFlower(inPosition);
            case 2: return objDonut(inPosition);
            case 3: return objElephantHead(inPosition);
            case 4: return objShell(inPosition);
            case 5: return objSombrero(inPosition);
            case 6: return objWineGlass(inPosition);
            default: return vec3(inPosition, 0.f);
    }
}

mat3 getTBN(vec2 inPos) {
    vec3 tx = (posCalc(inPos + vec2(delta, 0)) - posCalc(inPos - vec2(delta, 0))) / vec3(1.f, 1.f, 2 * delta);
    vec3 ty = (posCalc(inPos + vec2(0, delta)) - posCalc(inPos - vec2(0, delta))) / vec3(1.f, 1.f, 2 * delta);
    vec3 normal = cross(tx,ty);
    normal = normalize(inverse(transpose(mat3(u_View * u_Model))) * normal);
    vec3 vTan = normalize(tx);
    vec3 vBi = cross(normal,vTan);
    vTan = cross(vBi,normal);
    return mat3(vTan,vBi,normal);
}


void main() {
    //Texture
    texScale = vec2(1.f,1.f);
    texCoords = inPosition.xy;

    vec4 objectPosition;
    if(u_Grid == 1)
            objectPosition = u_View * vec4(posCalc(inPosition), 1.f);
    else
            objectPosition = u_View * u_Model * vec4(posCalc(inPosition), 1.f);

    //Light
    vec3 viewDirection = normalize(- objectPosition.xyz);

    vec4 lightPosition;
    if(u_Grid == 1)
            lightPosition = u_View * vec4(u_LightSource, 1.);
    else
            lightPosition = u_View * u_Model * vec4(u_LightSource, 1.);

    vec3 toLightVector = normalize(lightPosition.xyz - objectPosition.xyz);

    //TBN
    mat3 tbn = getTBN(inPosition);
    viewVec = transpose(tbn) * viewDirection;
    lightVec = transpose(tbn) * toLightVector;

    gl_Position = u_Proj * objectPosition;
}