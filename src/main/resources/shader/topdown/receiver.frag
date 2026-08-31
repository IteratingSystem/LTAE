#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_world;
varying float v_receiverHeight;
uniform sampler2D u_texture;
uniform sampler2D u_heightMap;
uniform mat4 u_projTrans;
uniform float u_heightRange;
uniform float u_pointMode;
uniform vec2 u_shadowDirection;
uniform float u_sunShadowLengthScale;
uniform vec2 u_lightPosition;
uniform float u_lightHeight;
uniform float u_lightRange;
uniform float u_time;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a * v_color.a;
    if (alpha < 0.01) {
        discard;
    }
    if (u_pointMode > 0.5
        && distance(v_world, u_lightPosition) > u_lightRange) {
        gl_FragColor = vec4(0.0);
        return;
    }

    float shadow = 0.0;
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
