#version 330

#import "classpath:/shaders/common/camera.glsl"

layout(location = 0) in vec3 vPosition;

uniform Camera uCamera;

void main() {
    gl_Position = uCamera.viewProj*vec4(vPosition, 1.0);
}