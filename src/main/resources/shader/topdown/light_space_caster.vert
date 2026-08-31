attribute vec3 a_position;
attribute vec2 a_texCoord;
uniform vec2 u_drawPosition;
uniform vec2 u_origin;
uniform vec2 u_size;
uniform vec2 u_scale;
uniform float u_rotation;
uniform float u_footY;
uniform float u_shadowDepth;
uniform vec2 u_uvBottomLeft;
uniform vec2 u_uvTopRight;
uniform vec3 u_lightAxisU;
uniform vec3 u_lightAxisV;
uniform vec3 u_lightAxisDepth;
uniform vec2 u_lightUvMin;
uniform vec2 u_lightUvSize;
uniform vec2 u_lightDepthRange;
varying vec2 v_texCoords;
varying float v_lightDepth;

void main() {
    vec2 local = a_position.xy * u_size;
    vec2 relative = (local - u_origin) * u_scale;
    float angle = radians(u_rotation);
    float cosine = cos(angle);
    float sine = sin(angle);
    vec2 rotated = vec2(
        relative.x * cosine - relative.y * sine,
        relative.x * sine + relative.y * cosine);
    vec2 spriteWorld = u_drawPosition + u_origin + rotated;
    float height = max(0.0, spriteWorld.y - u_footY);
    vec3 volumePoint = vec3(
        spriteWorld.x,
        u_footY + mix(-u_shadowDepth * 0.5,
            u_shadowDepth * 0.5, a_position.z),
        height);
    vec2 lightUv = vec2(
        dot(volumePoint, u_lightAxisU),
        dot(volumePoint, u_lightAxisV));
    float lightDepth = dot(volumePoint, u_lightAxisDepth);
    vec2 normalizedUv = (lightUv - u_lightUvMin) / u_lightUvSize;
    v_lightDepth = clamp(
        (lightDepth - u_lightDepthRange.x)
            / (u_lightDepthRange.y - u_lightDepthRange.x),
        0.0, 1.0);
    v_texCoords = mix(u_uvBottomLeft, u_uvTopRight, a_texCoord);
    gl_Position = vec4(
        normalizedUv * 2.0 - 1.0,
        v_lightDepth * 2.0 - 1.0, 1.0);
}
