#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_source;
uniform float u_sourceTexelY;

void main() {
    vec2 offset = vec2(0.0, u_sourceTexelY * 0.5);
    vec4 lower = texture2D(u_source, v_texCoords - offset);
    vec4 upper = texture2D(u_source, v_texCoords + offset);
    gl_FragColor = max(lower, upper);
}
