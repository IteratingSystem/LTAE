package org.ltae.light;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * 动态环境光总配置。未单独配置的地图使用默认配置。
 */
public final class AmbientLightConfig {
    private AmbientLightProfile defaultProfile;
    private final ObjectMap<String, AmbientLightProfile> mapProfiles = new ObjectMap<>();

    public AmbientLightConfig(AmbientLightProfile defaultProfile) {
        setDefaultProfile(defaultProfile);
    }

    public AmbientLightConfig setDefaultProfile(AmbientLightProfile defaultProfile) {
        if (defaultProfile == null) {
            throw new IllegalArgumentException("defaultProfile cannot be null");
        }
        this.defaultProfile = defaultProfile;
        return this;
    }

    public AmbientLightConfig setMapProfile(String mapName, AmbientLightProfile profile) {
        if (mapName == null || mapName.isBlank()) {
            throw new IllegalArgumentException("mapName cannot be blank");
        }
        if (profile == null) {
            throw new IllegalArgumentException("profile cannot be null");
        }
        mapProfiles.put(mapName, profile);
        return this;
    }

    public AmbientLightConfig removeMapProfile(String mapName) {
        if (mapName != null) {
            mapProfiles.remove(mapName);
        }
        return this;
    }

    public AmbientLightProfile getProfile(String mapName) {
        if (mapName == null) {
            return defaultProfile;
        }
        return mapProfiles.get(mapName, defaultProfile);
    }
}
