#version 330

#import "classpath:/shaders/common/camera.glsl"

layout(location = 0) in vec4 vPosition;

uniform Camera uCamera;

void main() {
    gl_Position = uCamera.viewProj*vPosition;
}