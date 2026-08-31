#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_source;
uniform float u_heightRange;
uniform float u_texelY;
uniform float u_worldPerTexelY;

void main() {
    float expanded = 0.0;
    float casterHeight = 0.0;
    for (int i = 0; i < 33; i++) {
        float texelOffset = float(i) - 16.0;
        float worldOffset = texelOffset * u_worldPerTexelY;
        vec2 sampleUv = v_texCoords
            + vec2(0.0, texelOffset * u_texelY);
        float inside = step(0.0, sampleUv.y) * step(sampleUv.y, 1.0);
        vec4 source = texture2D(u_source, clamp(sampleUv, 0.0, 1.0));
        float sourceDepth = source.g * u_heightRange;
        float covered = step(abs(worldOffset), sourceDepth * 0.5 + 0.001);
        expanded = max(expanded, source.r * covered * inside);
        casterHeight = max(
            casterHeight, source.b * covered * inside);
    }
    gl_FragColor = vec4(expanded, 0.0, casterHeight, expanded);
}
