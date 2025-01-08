#version 100

precision mediump float;

attribute vec3 aPosition;
attribute vec3 aNormal;

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat4 uNormalMatrix;

varying vec3 vFragPos;
varying vec3 vNormal;

void main() {
    vec4 worldPosition = uModelMatrix * vec4(aPosition, 1.0);
    vFragPos = vec3(worldPosition);
    vNormal = mat3(uNormalMatrix) * aNormal;

    gl_Position = uProjectionMatrix * uViewMatrix * worldPosition;
}
