#ifdef GL_ES
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_world;
varying float v_receiverHeight;
uniform sampler2D u_texture;
uniform sampler2D u_heightMap;
uniform sampler2D u_sunShadowMap;
uniform mat4 u_projTrans;
uniform vec2 u_entityId;
uniform float u_heightRange;
uniform float u_footY;
uniform float u_pointMode;
uniform vec2 u_shadowDirection;
uniform float u_sunShadowLengthScale;
uniform vec2 u_lightPosition;
uniform float u_lightHeight;
uniform float u_lightRange;
uniform float u_time;
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

float traceSunShadow() {
    vec3 receiver = vec3(v_world.x, u_footY, v_receiverHeight);
    vec2 lightUv = vec2(
        dot(receiver, u_lightAxisU),
        dot(receiver, u_lightAxisV));
    vec2 shadowUv = (lightUv - u_lightUvMin) / u_lightUvSize;
    float inside = step(0.0, shadowUv.x) * step(shadowUv.x, 1.0)
        * step(0.0, shadowUv.y) * step(shadowUv.y, 1.0);
    vec4 stored = texture2D(
        u_sunShadowMap, clamp(shadowUv, 0.0, 1.0));
    float occupied = step(0.5 / 255.0, max(stored.b, stored.a));
    float sameEntity = 1.0 - step(
        0.5 / 255.0, distance(stored.ba, u_entityId));
    float storedDepth = decodeDepth(stored.rg);
    float receiverDepth = (dot(receiver, u_lightAxisDepth)
        - u_lightDepthRange.x)
        / (u_lightDepthRange.y - u_lightDepthRange.x);
    return step(storedDepth + u_depthBias, receiverDepth)
        * occupied * (1.0 - sameEntity) * inside;
}

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a * v_color.a;
    if (alpha < 0.01) {
        discard;
    }
    float shadow = 0.0;
    if (u_pointMode < 0.5) {
        shadow = traceSunShadow();
    }
    if (u_pointMode > 0.5
        && distance(v_world, u_lightPosition) > u_lightRange) {
        gl_FragColor = vec4(0.0);
        return;
    }

    for (int i = 1; i < 24; i++) {
        float progress = float(i) / 23.0;
        float sunRayHeightDelta = u_heightRange * progress;
        float sunRayDistance = sunRayHeightDelta
            * u_sunShadowLengthScale;
        vec2 sunSample = v_world
            - u_shadowDirection * sunRayDistance
            + vec2(0.0, sunRayHeightDelta);
        vec2 pointSample = mix(
            v_world, u_lightPosition, progress)
            + vec2(0.0, u_lightHeight * progress);
        vec2 sampleWorld = mix(sunSample, pointSample, u_pointMode);
        float sunRayHeight = v_receiverHeight + sunRayHeightDelta;
        float pointRayHeight = mix(
            v_receiverHeight, u_lightHeight, progress);
        float rayHeight = mix(
            sunRayHeight, pointRayHeight, u_pointMode);
        vec4 sampleClip = u_projTrans
            * vec4(sampleWorld, 0.0, 1.0);
        vec2 sampleUv = sampleClip.xy / sampleClip.w * 0.5 + 0.5;
        float inside = step(0.0, sampleUv.x)
            * step(sampleUv.x, 1.0)
            * step(0.0, sampleUv.y)
            * step(sampleUv.y, 1.0);
        vec4 obstacle = texture2D(
            u_heightMap, clamp(sampleUv, 0.0, 1.0));
        float obstacleHeight = obstacle.r * u_heightRange * inside;
        float edgeMotion = sin(u_time * 1.8
            + sampleWorld.x * 0.09
            + sampleWorld.y * 0.11) * 0.1;
        float occlusion = smoothstep(
            0.3 + edgeMotion, 0.75 + edgeMotion,
            obstacleHeight - rayHeight);
        shadow = max(shadow, occlusion);
    }
    gl_FragColor = vec4(shadow * alpha);
}
