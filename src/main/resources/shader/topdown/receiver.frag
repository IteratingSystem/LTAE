#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_world;
varying float v_receiverHeight;
uniform sampler2D u_texture;
uniform sampler2D u_groundShadow;
uniform mat4 u_projTrans;
uniform float u_footY;
uniform float u_heightRange;
uniform float u_pointMode;
uniform vec2 u_shadowDirection;
uniform float u_sunShadowLengthScale;
uniform vec2 u_lightPosition;
uniform float u_lightHeight;
uniform float u_lightRange;
uniform float u_time;

void main() {
    float alpha = texture2D(u_texture, v_texCoords).a * v_color.a;
    if (alpha < 0.01) {
        discard;
    }
    if (u_pointMode > 0.5
        && distance(v_world, u_lightPosition) > u_lightRange) {
        gl_FragColor = vec4(0.0);
        return;
    }

    vec2 receiverGround = vec2(v_world.x, u_footY);
    vec2 sunProjection = receiverGround
        + u_shadowDirection * v_receiverHeight * u_sunShadowLengthScale;
    float pointScale = v_receiverHeight
        / max(u_lightHeight - v_receiverHeight, 1.0);
    vec2 pointProjection = receiverGround
        + (receiverGround - u_lightPosition) * pointScale;
    vec2 groundProjection = mix(sunProjection, pointProjection, u_pointMode);
    vec4 groundClip = u_projTrans * vec4(groundProjection, 0.0, 1.0);
    vec2 groundUv = groundClip.xy / groundClip.w * 0.5 + 0.5;
    float inside = step(0.0, groundUv.x)
        * step(groundUv.x, 1.0)
        * step(0.0, groundUv.y)
        * step(groundUv.y, 1.0);
    vec4 groundShadow = texture2D(
        u_groundShadow, clamp(groundUv, 0.0, 1.0));
    float casterHeight = groundShadow.b * u_heightRange * inside;
    float edgeMotion = sin(u_time * 1.8
        + groundProjection.x * 0.09
        + groundProjection.y * 0.11) * 0.1;
    float shadow = smoothstep(
        0.3 + edgeMotion, 0.75 + edgeMotion,
        casterHeight - v_receiverHeight);
    gl_FragColor = vec4(shadow * alpha);
}
