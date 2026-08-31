#ifdef GL_ES
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif
#endif
varying vec2 v_texCoords;
uniform sampler2D u_shadowMap;
uniform mat4 u_invProjTrans;
uniform vec3 u_lightAxisU;
uniform vec3 u_lightAxisV;
uniform vec3 u_lightAxisDepth;
uniform vec2 u_lightUvMin;
uniform vec2 u_lightUvSize;
uniform vec2 u_lightDepthRange;
uniform float u_depthBias;

float decodeDepth(vec2 encoded) {
    return encoded.x + encoded.y / 255.0;
}

void main() {
    vec2 clipPosition = v_texCoords * 2.0 - 1.0;
    vec4 worldPosition = u_invProjTrans
        * vec4(clipPosition, 0.0, 1.0);
    worldPosition /= worldPosition.w;
    vec3 receiver = vec3(worldPosition.xy, 0.0);
    vec2 lightUv = vec2(
        dot(receiver, u_lightAxisU),
        dot(receiver, u_lightAxisV));
    vec2 shadowUv = (lightUv - u_lightUvMin) / u_lightUvSize;
    float inside = step(0.0, shadowUv.x) * step(shadowUv.x, 1.0)
        * step(0.0, shadowUv.y) * step(shadowUv.y, 1.0);
    vec4 stored = texture2D(u_shadowMap, clamp(shadowUv, 0.0, 1.0));
    float occupied = step(0.5 / 255.0, max(stored.b, stored.a));
    float storedDepth = decodeDepth(stored.rg);
    float receiverDepth = (dot(receiver, u_lightAxisDepth)
        - u_lightDepthRange.x)
        / (u_lightDepthRange.y - u_lightDepthRange.x);
    float shadow = step(storedDepth + u_depthBias, receiverDepth)
        * occupied * inside;
    gl_FragColor = vec4(shadow, 0.0, 0.0, shadow);
}
