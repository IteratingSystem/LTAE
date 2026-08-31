package org.ltae.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * 云影外观与地图范围配置。
 */
public final class CloudShadowConfig {
    private final ObjectSet<String> enabledMaps = new ObjectSet<>();
    private String noiseName = "cloud";
    private float opacity = 0.18f;
    private float coverageThreshold = 0.37f;
    private float edgeSoftness = 0.045f;
    private float worldSize = 2200f;
    private float driftMultiplier = 14f;

    public String getNoiseName() {
        return noiseName;
    }

    public CloudShadowConfig setNoiseName(String noiseName) {
        if (noiseName == null || noiseName.isBlank()) {
            throw new IllegalArgumentException("noiseName cannot be blank");
        }
        this.noiseName = noiseName;
        return this;
    }

    public float getOpacity() {
        return opacity;
    }

    public CloudShadowConfig setOpacity(float opacity) {
        this.opacity = MathUtils.clamp(opacity, 0f, 1f);
        return this;
    }

    public float getCoverageThreshold() {
        return coverageThreshold;
    }

    public CloudShadowConfig setCoverageThreshold(float threshold) {
        coverageThreshold = MathUtils.clamp(threshold, 0f, 1f);
        return this;
    }

    public float getEdgeSoftness() {
        return edgeSoftness;
    }

    public CloudShadowConfig setEdgeSoftness(float edgeSoftness) {
        this.edgeSoftness = MathUtils.clamp(edgeSoftness, 0.001f, 0.5f);
        return this;
    }

    public float getWorldSize() {
        return worldSize;
    }

    public CloudShadowConfig setWorldSize(float worldSize) {
        if (worldSize <= 0f) {
            throw new IllegalArgumentException("worldSize must be greater than zero");
        }
        this.worldSize = worldSize;
        return this;
    }

    public float getDriftMultiplier() {
        return driftMultiplier;
    }

    public CloudShadowConfig setDriftMultiplier(float driftMultiplier) {
        if (driftMultiplier < 0f) {
            throw new IllegalArgumentException(
                "driftMultiplier cannot be negative");
        }
        this.driftMultiplier = driftMultiplier;
        return this;
    }

    public CloudShadowConfig setEnabledMaps(String... mapNames) {
        enabledMaps.clear();
        if (mapNames == null) {
            return this;
        }
        for (String mapName : mapNames) {
            if (mapName != null && !mapName.isBlank()) {
                enabledMaps.add(mapName);
            }
        }
        return this;
    }

    public boolean isEnabled(String mapName) {
        return enabledMaps.isEmpty() || enabledMaps.contains(mapName);
    }
}
