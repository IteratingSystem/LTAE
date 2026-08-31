#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_shadowDepth;
uniform float u_heightRange;
uniform float u_sunParallelFill;

void main() {
    float alpha = max(
        texture2D(u_texture, v_texCoords).a,
        u_sunParallelFill);
    if (alpha < 0.01) {
        discard;
    }
    gl_FragColor = vec4(
        alpha,
        clamp(u_shadowDepth / u_heightRange, 0.0, 1.0),
        0.0, alpha);
}
