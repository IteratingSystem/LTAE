#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_cloudNoise;
uniform mat4 u_invProjTrans;
uniform vec2 u_windDisplacement;
uniform float u_worldSize;
uniform float u_driftMultiplier;
uniform float u_coverageThreshold;
uniform float u_edgeSoftness;
uniform float u_opacity;

void main() {
    vec2 clipPosition = v_texCoords * 2.0 - 1.0;
    vec4 world = u_invProjTrans * vec4(clipPosition, 0.0, 1.0);
    vec2 worldPosition = world.xy / world.w;
    vec2 cloudPosition = worldPosition
        + u_windDisplacement * u_driftMultiplier;
    vec2 cloudUv = fract(cloudPosition / u_worldSize);
    float density = texture2D(u_cloudNoise, cloudUv).r;
    float shadow = smoothstep(
        u_coverageThreshold - u_edgeSoftness,
        u_coverageThreshold + u_edgeSoftness,
        density);
    gl_FragColor = vec4(0.0, 0.0, 0.0, shadow * u_opacity);
}
