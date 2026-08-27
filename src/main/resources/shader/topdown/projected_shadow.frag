#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a;
    if (alpha < 0.01) {
        discard;
    }
    gl_FragColor = vec4(alpha);
}
