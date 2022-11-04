#version 330

in vec2 texCoords;
in vec3 lightVec;
in vec3 viewVec;

uniform float u_ColorR;
uniform sampler2D textureBase;
uniform sampler2D textureNormal;
uniform sampler2D textureHeight;

out vec4 outColor;

vec4 ambientColor = vec4(0.1f, 0.1f, 0.1f, 1.f);
vec4 diffuseColor = vec4(0.5f, 0.5f, 0.5f, 1.f);
vec4 specularColor = vec4(0.5f, 0.5f, 0.5f, 1.f);

void main() {
    //Parallax mapping calculations
    float height = texture(textureHeight, texCoords).r;
    float scaleL = 0.02;
    float scaleK = 0.f;
    float v = height * scaleL + scaleK;
    vec3 eye = normalize(viewVec);
    vec2 offset = eye.xy * v;

    //Texture readings with parallax offset
    vec4 baseColor = texture(textureBase, texCoords);
    vec4 textureColor = texture(textureNormal, texCoords + offset);

    //Tangent space
    vec3 normal = textureColor.rgb * 2.f - 1.f;

    //Ligth calculations
    vec3 ld = normalize(lightVec);
    vec3 nd = normalize(normal);
    float NDotL = max(dot(nd, ld), 0.f);
    float NdotHV = max(0.f, dot(nd, normalize(ld + viewVec)));

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = specularColor * pow(NdotHV,10.0);

    outColor.rgb = baseColor.xyz * (diffuse.rgb + ambient.rgb) + specular.rgb;
}
