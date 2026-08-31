attribute vec2 a_position;
attribute float a_casterHeight;
uniform mat4 u_projTrans;
uniform float u_heightRange;
varying float v_casterHeight;

void main() {
    v_casterHeight = clamp(a_casterHeight / u_heightRange, 0.0, 1.0);
    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}
