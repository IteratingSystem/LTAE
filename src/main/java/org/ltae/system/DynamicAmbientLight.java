package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import org.ltae.light.AmbientLightConfig;
import org.ltae.light.AmbientLightProfile;
import org.ltae.light.AmbientLightTimeSource;

/**
 * 根据游戏时间与当前地图配置更新环境光。
 */
public class DynamicAmbientLight extends BaseSystem {
    private LightSystem lightSystem;
    private TiledMapSystem tiledMapSystem;

    private final AmbientLightTimeSource timeSource;
    private final AmbientLightConfig config;
    private final Color current = new Color();
    private final Color sampled = new Color();

    public DynamicAmbientLight(AmbientLightTimeSource timeSource, AmbientLightConfig config) {
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        this.timeSource = timeSource;
        this.config = config;
    }

    @Override
    protected void processSystem() {
        AmbientLightProfile profile = config.getProfile(tiledMapSystem.getCurrent());
        profile.sample(timeSource.getHour(), timeSource.getMinute(), sampled);
        if (current.equals(sampled)) {
            return;
        }
        current.set(sampled);
        lightSystem.setAmbientLight(current);
    }
}
