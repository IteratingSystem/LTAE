#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec4 v_color;      // 从顶点着色器接收
varying vec2 v_texCoords;  // 从顶点着色器接收

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = texColor * v_color; // 正常贴图 * 顶点色
}

