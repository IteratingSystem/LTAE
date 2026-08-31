#ifdef GL_ES
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif
#endif
varying vec2 v_texCoords;
varying float v_lightDepth;
uniform sampler2D u_texture;
uniform vec2 u_entityId;

vec2 encodeDepth(float depth) {
    float safeDepth = min(depth, 1.0 - 1.0 / 65535.0);
    vec2 encoded = fract(vec2(1.0, 255.0) * safeDepth);
    encoded.x -= encoded.y / 255.0;
    return encoded;
}

void main() {
    if (texture2D(u_texture, v_texCoords).a < 0.01) {
        discard;
    }
    gl_FragColor = vec4(encodeDepth(v_lightDepth), u_entityId);
}
