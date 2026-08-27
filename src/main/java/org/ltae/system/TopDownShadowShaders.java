package org.ltae.system;

/**
 * 俯视角阴影渲染管线使用的GLSL源码。
 */
final class TopDownShadowShaders {
    static final String SCREEN_VERTEX_SHADER = """
        attribute vec2 a_position;
        attribute vec2 a_texCoord;
        varying vec2 v_texCoords;

        void main() {
            v_texCoords = a_texCoord;
            gl_Position = vec4(a_position, 0.0, 1.0);
        }
        """;

    static final String SPRITE_VERTEX_SHADER = """
        attribute vec4 a_position;
        attribute vec4 a_color;
        attribute vec2 a_texCoord0;
        uniform mat4 u_projTrans;
        varying vec4 v_color;
        varying vec2 v_texCoords;
        varying vec2 v_world;

        void main() {
            v_color = a_color;
            v_texCoords = a_texCoord0;
            v_world = a_position.xy;
            gl_Position = u_projTrans * a_position;
        }
        """;

    static final String HEIGHT_MAP_FRAGMENT_SHADER = """
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
        """;

    static final String ENTITY_MASK_FRAGMENT_SHADER = """
        #ifdef GL_ES
        precision mediump float;
        #endif
        varying vec4 v_color;
        varying vec2 v_texCoords;
        uniform sampler2D u_texture;

        void main() {
            float alpha = texture2D(u_texture, v_texCoords).a * v_color.a;
            if (alpha < 0.01) {
                discard;
            }
            gl_FragColor = vec4(alpha);
        }
        """;

    static final String PROJECTED_SHADOW_VERTEX_SHADER = """
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
            vec2 sunProjection = ground + u_shadowDirection * pixelHeight;
            float pointScale = pixelHeight
                / max(u_lightHeight - pixelHeight, 1.0);
            vec2 pointProjection = ground
                + (ground - u_lightPosition) * pointScale;
            vec2 projected = mix(sunProjection, pointProjection, u_pointMode);
            v_texCoords = mix(u_uvBottomLeft, u_uvTopRight, a_texCoord);
            gl_Position = u_projTrans * vec4(projected, 0.0, 1.0);
        }
        """;

    static final String PROJECTED_SHADOW_FRAGMENT_SHADER = """
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
        """;

    static final String RECEIVER_VERTEX_SHADER = """
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
        """;

    static final String RECEIVER_FRAGMENT_SHADER = """
        #ifdef GL_ES
        precision mediump float;
        #endif
        varying vec4 v_color;
        varying vec2 v_texCoords;
        varying vec2 v_world;
        varying float v_receiverHeight;
        uniform sampler2D u_texture;
        uniform sampler2D u_heightMap;
        uniform mat4 u_projTrans;
        uniform vec2 u_receiverId;
        uniform float u_heightRange;
        uniform float u_pointMode;
        uniform vec2 u_shadowDirection;
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

            float shadow = 0.0;
            for (int i = 1; i < 24; i++) {
                float progress = float(i) / 23.0;
                float rayDistance = u_heightRange * progress;
                vec2 sunSample = v_world - u_shadowDirection * rayDistance;
                vec2 pointSample = mix(v_world, u_lightPosition, progress);
                vec2 sampleWorld = mix(sunSample, pointSample, u_pointMode);
                float sunRayHeight = v_receiverHeight + rayDistance;
                float pointRayHeight = mix(
                    v_receiverHeight, u_lightHeight, progress);
                float rayHeight = mix(
                    sunRayHeight, pointRayHeight, u_pointMode);
                vec4 sampleClip = u_projTrans
                    * vec4(sampleWorld, 0.0, 1.0);
                vec2 sampleUv = sampleClip.xy / sampleClip.w * 0.5 + 0.5;
                float inside = step(0.0, sampleUv.x)
                    * step(sampleUv.x, 1.0)
                    * step(0.0, sampleUv.y)
                    * step(sampleUv.y, 1.0);
                vec4 obstacle = texture2D(
                    u_heightMap, clamp(sampleUv, 0.0, 1.0));
                float isSelf = 1.0 - step(
                    0.002, distance(obstacle.gb, u_receiverId));
                float obstacleHeight = obstacle.r * u_heightRange * inside;
                float edgeMotion = sin(u_time * 1.8
                    + sampleWorld.x * 0.09
                    + sampleWorld.y * 0.11) * 0.1;
                float occlusion = smoothstep(
                    0.3 + edgeMotion, 0.75 + edgeMotion,
                    obstacleHeight - rayHeight);
                shadow = max(shadow, occlusion * (1.0 - isSelf));
            }
            gl_FragColor = vec4(shadow * alpha);
        }
        """;

    static final String SUN_COMPOSITE_FRAGMENT_SHADER = """
        #ifdef GL_ES
        precision mediump float;
        #endif
        varying vec2 v_texCoords;
        uniform sampler2D u_groundShadow;
        uniform sampler2D u_entityMask;
        uniform sampler2D u_receiverShadow;
        uniform vec2 u_shadowTexel;
        uniform float u_shadowOpacity;
        uniform float u_time;

        float softShadow(sampler2D textureSampler, vec2 uv) {
            float phase = dot(floor(uv * vec2(120.0, 68.0)),
                vec2(0.067, 0.113));
            vec2 jitter = vec2(
                sin(u_time * 1.7 + phase),
                cos(u_time * 1.3 + phase)) * u_shadowTexel * 0.65;
            float center = texture2D(textureSampler, uv).r;
            float positive = texture2D(textureSampler, uv + jitter).r;
            float negative = texture2D(textureSampler, uv - jitter).r;
            return center * 0.5 + (positive + negative) * 0.25;
        }

        void main() {
            float entity = texture2D(u_entityMask, v_texCoords).r;
            float ground = softShadow(u_groundShadow, v_texCoords)
                * (1.0 - entity);
            float receiver = softShadow(u_receiverShadow, v_texCoords);
            float shadow = max(ground, receiver);
            gl_FragColor = vec4(0.02, 0.035, 0.06,
                shadow * u_shadowOpacity);
        }
        """;

    static final String POINT_COMPOSITE_FRAGMENT_SHADER = """
        #ifdef GL_ES
        precision mediump float;
        #endif
        varying vec2 v_texCoords;
        uniform sampler2D u_lightMap;
        uniform sampler2D u_groundShadow;
        uniform sampler2D u_entityMask;
        uniform sampler2D u_receiverShadow;
        uniform vec2 u_shadowTexel;
        uniform float u_time;

        float softShadow(sampler2D textureSampler, vec2 uv) {
            float phase = dot(floor(uv * vec2(120.0, 68.0)),
                vec2(0.067, 0.113));
            vec2 jitter = vec2(
                sin(u_time * 1.7 + phase),
                cos(u_time * 1.3 + phase)) * u_shadowTexel * 0.65;
            float center = texture2D(textureSampler, uv).r;
            float positive = texture2D(textureSampler, uv + jitter).r;
            float negative = texture2D(textureSampler, uv - jitter).r;
            return center * 0.5 + (positive + negative) * 0.25;
        }

        void main() {
            vec4 light = texture2D(u_lightMap, v_texCoords);
            if (light.a < 0.001) {
                gl_FragColor = vec4(0.0);
                return;
            }
            float entity = texture2D(u_entityMask, v_texCoords).r;
            float ground = softShadow(u_groundShadow, v_texCoords)
                * (1.0 - entity);
            float receiver = softShadow(u_receiverShadow, v_texCoords);
            float visibility = 1.0 - max(ground, receiver);
            gl_FragColor = vec4(
                light.rgb * light.a * visibility,
                light.a * visibility);
        }
        """;

    private TopDownShadowShaders() {
    }
}
