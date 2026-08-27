attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
uniform float u_footY;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_world;
varying float v_receiverHeight;

void main() {
    vec4 clip = u_projTrans * a_position;
    gl_Position = clip;
    v_color = a_color;
    v_texCoords = a_texCoord0;
    v_world = a_position.xy;
    v_receiverHeight = max(0.0, a_position.y - u_footY);
}
