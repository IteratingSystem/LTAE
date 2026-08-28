package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.MathUtils;
import org.ltae.light.AmbientLightTimeSource;
import org.ltae.light.SunLightConfig;

/**
 * 根据游戏时间更新俯视角太阳方向和高度。
 */
public class DynamicSunLight extends BaseSystem {
    private TopDownShadowSystem topDownShadowSystem;

    private final AmbientLightTimeSource timeSource;
    private final SunLightConfig config;

    public DynamicSunLight(AmbientLightTimeSource timeSource) {
        this(timeSource, new SunLightConfig());
    }

    public DynamicSunLight(AmbientLightTimeSource timeSource,
                           SunLightConfig config) {
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
        float currentHour = timeSource.getHour()
            + timeSource.getMinute() / 60f;
        float elapsedHours = (currentHour - config.getReferenceHour() + 24f)
            % 24f;
        float dailyProgress = elapsedHours / 24f;
        float sunBearing = config.getReferenceBearingDegree()
            + config.getDailyBearingSweepDegree() * dailyProgress;
        float elevationProgress = Math.abs(MathUtils.sin(
            MathUtils.PI * elapsedHours / 12f));
        float elevation = MathUtils.lerp(
            config.getMinimumElevationDegree(),
            config.getMaximumElevationDegree(), elevationProgress);
        topDownShadowSystem.getSunLight().setSunBearingDegree(sunBearing);
        topDownShadowSystem.getSunLight().setElevationDegree(elevation);
    }
}
