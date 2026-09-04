package org.worldloom.light;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * 俯视角阴影系统配置。
 */
public final class TopDownShadowConfig {
    private float sunShadowOpacity = 0.52f;
    private float heightRange = 256f;
    private float resolutionScale = 0.5f;
    private boolean defaultSunEnabled = true;
    private final ObjectMap<String, Boolean> mapSunEnabled = new ObjectMap<>();

    public float getSunShadowOpacity() {
        return sunShadowOpacity;
    }

    public TopDownShadowConfig setSunShadowOpacity(float sunShadowOpacity) {
        this.sunShadowOpacity = MathUtils.clamp(sunShadowOpacity, 0f, 1f);
        return this;
    }

    public float getHeightRange() {
        return heightRange;
    }

    public TopDownShadowConfig setHeightRange(float heightRange) {
        if (heightRange <= 0f) {
            throw new IllegalArgumentException("heightRange must be greater than zero");
        }
        this.heightRange = heightRange;
        return this;
    }

    public float getResolutionScale() {
        return resolutionScale;
    }

    public TopDownShadowConfig setResolutionScale(float resolutionScale) {
        if (resolutionScale <= 0f || resolutionScale > 1f) {
            throw new IllegalArgumentException(
                "resolutionScale must be greater than zero and at most one");
        }
        this.resolutionScale = resolutionScale;
        return this;
    }

    public boolean isSunEnabled(String mapName) {
        if (mapName == null) {
            return defaultSunEnabled;
        }
        return mapSunEnabled.get(mapName, defaultSunEnabled);
    }

    public TopDownShadowConfig setDefaultSunEnabled(boolean enabled) {
        defaultSunEnabled = enabled;
        return this;
    }

    public TopDownShadowConfig setMapSunEnabled(String mapName, boolean enabled) {
        if (mapName == null || mapName.isBlank()) {
            throw new IllegalArgumentException("mapName cannot be blank");
        }
        mapSunEnabled.put(mapName, enabled);
        return this;
    }

    public TopDownShadowConfig removeMapSunEnabled(String mapName) {
        if (mapName != null) {
            mapSunEnabled.remove(mapName);
        }
        return this;
    }
}
