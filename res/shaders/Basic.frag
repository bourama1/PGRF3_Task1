#version 330

in vec2 texCoords;
in vec3 toLightVector;

uniform float u_ColorR;
uniform sampler2D textureBase;
uniform sampler2D textureNormal;

out vec4 outColor;

vec4 ambientColor = vec4(0.5f, 0.5f, 0.5f, 1.f);
vec4 diffuseColor = vec4(0.5f, 0.5f, 0.5f, 1.f);

void main() {
    vec4 baseColor = texture(textureBase, texCoords);

    vec4 textureColor = texture(textureNormal, texCoords);

    // Tangent space
    vec3 normal = textureColor.rgb * 2.f - 1.f;

    //Diffuse ligth
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normal);
    float NDotL = max(dot(nd, ld), 0.f);

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = vec4(0);

    outColor = (ambient + diffuse + specular) * baseColor;
}
