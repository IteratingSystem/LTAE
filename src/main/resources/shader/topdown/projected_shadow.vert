attribute vec2 a_position;
attribute vec2 a_texCoord;
uniform mat4 u_projTrans;
uniform vec2 u_drawPosition;
uniform vec2 u_origin;
uniform vec2 u_size;
uniform vec2 u_scale;
uniform float u_rotation;
uniform float u_footY;
uniform vec2 u_uvBottomLeft;
uniform vec2 u_uvTopRight;
uniform float u_pointMode;
uniform vec2 u_shadowDirection;
uniform float u_sunShadowLengthScale;
uniform float u_sunProjectionSeedThickness;
uniform vec2 u_lightPosition;
uniform float u_lightHeight;
varying vec2 v_texCoords;

void main() {
    vec2 local = a_position * u_size;
    vec2 relative = (local - u_origin) * u_scale;
    float radians = radians(u_rotation);
    float cosine = cos(radians);
    float sine = sin(radians);
    vec2 rotated = vec2(
        relative.x * cosine - relative.y * sine,
        relative.x * sine + relative.y * cosine);
    vec2 spriteWorld = u_drawPosition + u_origin + rotated;
    float pixelHeight = max(0.0, spriteWorld.y - u_footY);
    vec2 ground = vec2(spriteWorld.x, u_footY);
    vec2 sunProjection = ground
        + u_shadowDirection * pixelHeight * u_sunShadowLengthScale;
    sunProjection.y += (a_position.x - 0.5)
        * u_sunProjectionSeedThickness;
    float pointScale = pixelHeight
        / max(u_lightHeight - pixelHeight, 1.0);
    vec2 pointProjection = ground
        + (ground - u_lightPosition) * pointScale;
    vec2 projected = mix(sunProjection, pointProjection, u_pointMode);
    v_texCoords = mix(u_uvBottomLeft, u_uvTopRight, a_texCoord);
    gl_Position = u_projTrans * vec4(projected, 0.0, 1.0);
}
