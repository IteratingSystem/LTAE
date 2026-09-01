package org.worldloom.audio;

import com.badlogic.gdx.math.MathUtils;

/**
 * 俯视角二维声音的距离衰减与左右声像计算。
 */
public final class AudioSpatializer {
    private AudioSpatializer() {
    }

    public static SpatialResult calculate(float listenerX, float listenerY,
                                          float sourceX, float sourceY,
                                          float minDistance,
                                          float maxDistance,
                                          float rolloff,
                                          float panningStrength) {
        if (minDistance < 0f || maxDistance <= minDistance || rolloff <= 0f
            || panningStrength < 0f) {
            throw new IllegalArgumentException("invalid spatial parameters");
        }

        float dx = sourceX - listenerX;
        float dy = sourceY - listenerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float attenuation;
        if (distance <= minDistance) {
            attenuation = 1f;
        } else if (distance >= maxDistance) {
            attenuation = 0f;
        } else {
            float normalized = (distance - minDistance)
                / (maxDistance - minDistance);
            attenuation = (float) Math.pow(1f - normalized, rolloff);
        }

        float panRange = Math.max(minDistance, maxDistance * 0.5f);
        float pan = MathUtils.clamp(dx / panRange * panningStrength, -1f, 1f);
        return new SpatialResult(attenuation, pan);
    }

    public record SpatialResult(float attenuation, float pan) {
    }
}
