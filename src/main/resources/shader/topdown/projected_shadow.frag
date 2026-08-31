#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
varying float v_casterHeight;
uniform sampler2D u_texture;
uniform float u_shadowDepth;
uniform float u_heightRange;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a;
    if (alpha < 0.01) {
        discard;
    }
    gl_FragColor = vec4(
        alpha,
        clamp(u_shadowDepth / u_heightRange, 0.0, 1.0),
        clamp(v_casterHeight / u_heightRange, 0.0, 1.0), alpha);
}
