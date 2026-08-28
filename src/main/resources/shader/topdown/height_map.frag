#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_world;
uniform sampler2D u_texture;
uniform float u_footY;
uniform float u_heightRange;
uniform vec2 u_receiverId;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a * v_color.a;
    if (alpha < 0.01) {
        discard;
    }
    float height = max(0.0, v_world.y - u_footY);
    gl_FragColor = vec4(
        clamp(height / u_heightRange, 0.0, 1.0),
        u_receiverId.x, u_receiverId.y, 1.0);
}
