#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_source;
uniform float u_maxDepth;
uniform float u_heightRange;
uniform float u_inverseWorldHeight;

void main() {
    float expanded = 0.0;
    for (int i = 0; i < 17; i++) {
        float ratio = float(i) / 16.0 * 2.0 - 1.0;
        float worldOffset = ratio * u_maxDepth * 0.5;
        vec2 sampleUv = v_texCoords
            + vec2(0.0, worldOffset * u_inverseWorldHeight);
        float inside = step(0.0, sampleUv.y) * step(sampleUv.y, 1.0);
        vec4 source = texture2D(u_source, clamp(sampleUv, 0.0, 1.0));
        float sourceDepth = source.g * u_heightRange;
        float covered = step(abs(worldOffset), sourceDepth * 0.5 + 0.001);
        expanded = max(expanded, source.r * covered * inside);
    }
    gl_FragColor = vec4(expanded, 0.0, 0.0, expanded);
}
