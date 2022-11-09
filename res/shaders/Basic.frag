#version 330

in vec2 texCoords;
in vec3 lightVec;
in vec3 viewVec;

uniform float u_ColorR;
uniform sampler2D textureBase;
uniform sampler2D textureNormal;
uniform sampler2D textureHeight;

out vec4 outColor;

const float constantAttenuation = 1.0;
const float linearAttenuation = 0.1;
const float quadraticAttenuation = 0.01;

vec4 ambientColor = vec4(0.1f, 0.1f, 0.1f, 1.f);
vec4 diffuseColor = vec4(0.7f, 0.7f, 0.7f, 1.f);
vec4 specularColor = vec4(0.7f, 0.7f, 0.7f, 1.f);

void main() {
    //Texture calc
    vec2 coord = mod(texCoords * vec2(2.0, 2.0), vec2(1.0, 1.0));
    //Parallax mapping calculations
    float height = texture(textureHeight, coord).r;
    float scaleL = 0.04f;
    float scaleK = -0.02f;
    float v = height * scaleL + scaleK;
    vec3 eye = normalize(viewVec);
    vec2 offset = eye.xy * v;

    //Texture readings with parallax offset
    vec4 baseColor = texture(textureBase, coord);
    vec4 textureColor = texture(textureNormal, coord + offset);

    //Tangent space
    vec3 normal = textureColor.rgb * 2.f - 1.f;

    //Ligth calculations
    vec3 ld = normalize(lightVec);
    vec3 nd = normalize(normal);
    float NDotL = max(dot(nd, ld), 0.f);
    float NdotHV = max(0.f, dot(nd, normalize(ld + viewVec)));

    //attenuation
    float dis = length(lightVec);
    float att = 1.0 / (constantAttenuation + linearAttenuation * dis + quadraticAttenuation * pow(dis,2.0f));
    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = specularColor * pow(NdotHV,10.0);

    outColor = ambient * baseColor + att * (diffuse * baseColor + specular);
}
