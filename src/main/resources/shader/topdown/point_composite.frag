#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_lightMap;
uniform sampler2D u_groundShadow;
uniform sampler2D u_entityMask;
uniform sampler2D u_receiverShadow;
uniform vec2 u_shadowTexel;
uniform float u_time;

float softShadow(sampler2D textureSampler, vec2 uv) {
    float phase = dot(floor(uv * vec2(120.0, 68.0)),
        vec2(0.067, 0.113));
    vec2 jitter = vec2(
        sin(u_time * 1.7 + phase),
        cos(u_time * 1.3 + phase)) * u_shadowTexel * 0.65;
    float center = texture2D(textureSampler, uv).r;
    float positive = texture2D(textureSampler, uv + jitter).r;
    float negative = texture2D(textureSampler, uv - jitter).r;
    return center * 0.5 + (positive + negative) * 0.25;
}

void main() {
    vec4 light = texture2D(u_lightMap, v_texCoords);
    if (light.a < 0.001) {
        gl_FragColor = vec4(0.0);
        return;
    }
    float entity = texture2D(u_entityMask, v_texCoords).r;
    float ground = softShadow(u_groundShadow, v_texCoords)
        * (1.0 - entity);
    float receiver = softShadow(u_receiverShadow, v_texCoords);
    float visibility = 1.0 - max(ground, receiver);
    gl_FragColor = vec4(
        light.rgb * visibility,
        light.a * visibility);
}
