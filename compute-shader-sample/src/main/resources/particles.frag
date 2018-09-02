#version 330

out vec4 finalColor;

uniform vec4 uColor;

void main() {
    finalColor = vec4(uColor.rgb, 0.3);
}