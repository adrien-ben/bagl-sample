#version 330

in vec2 passCoords;
in vec4 passColor;

out vec4 color;

uniform sampler2D uTexture;

void main() {
    float alpha = texture2D(uTexture, passCoords).a;
    color = vec4(vec3(alpha), 1.0);
}