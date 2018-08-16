#version 330

in vec2 passCoords;
in vec4 passColor;

out vec4 color;

uniform float minDepth;
uniform float maxDepth;
uniform sampler2D uTexture;

float linearizeDepth(float depth, float n, float f) {
    return (2 * n) / (f + n - depth * (f - n));
}

void main() {
    float depth = texture2D(uTexture, passCoords).r;
    float linearDepth = linearizeDepth(depth, minDepth, maxDepth);
    color = vec4(vec3(linearDepth), 1.0);
}