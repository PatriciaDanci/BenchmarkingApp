#version 100

precision mediump float;

varying vec3 vFragPos;
varying vec3 vNormal;

uniform vec3 uLightPos[7];
uniform vec3 uLightColor[7];
uniform vec4 uObjectColor;
uniform vec3 uViewPos;

void main() {
    vec3 norm = normalize(vNormal);
    vec3 viewDir = normalize(uViewPos - vFragPos);

    vec3 ambient = vec3(0.1) * uObjectColor.rgb;
    vec3 result = ambient;

    for (int i = 0; i < 7; i++) {
        // Diffuse component
        vec3 lightDir = normalize(uLightPos[i] - vFragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * uLightColor[i];

        // Specular component
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0); // Shininess = 32
        vec3 specular = spec * uLightColor[i];

        result += diffuse + specular;
    }

    gl_FragColor = vec4(result, uObjectColor.a);
}
