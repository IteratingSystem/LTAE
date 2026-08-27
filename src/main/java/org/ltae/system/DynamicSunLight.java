package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.MathUtils;
import org.ltae.light.AmbientLightTimeSource;

/**
 * 根据游戏时间更新俯视角太阳方向和阴影可见强度。
 */
public class DynamicSunLight extends BaseSystem {
    private TopDownShadowSystem topDownShadowSystem;

    private final AmbientLightTimeSource timeSource;
    private final float sunriseHour;
    private final float sunsetHour;
    private final float sunriseDirectionDegree;
    private final float directionSweepDegree;

    public DynamicSunLight(AmbientLightTimeSource timeSource) {
        this(timeSource, 6f, 18f, 180f, 180f);
    }

    public DynamicSunLight(AmbientLightTimeSource timeSource,
                           float sunriseHour, float sunsetHour,
                           float sunriseDirectionDegree,
                           float directionSweepDegree) {
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource cannot be null");
        }
        if (sunriseHour < 0f || sunsetHour > 24f
            || sunriseHour >= sunsetHour) {
            throw new IllegalArgumentException(
                "sunriseHour must be earlier than sunsetHour");
        }
        this.timeSource = timeSource;
        this.sunriseHour = sunriseHour;
        this.sunsetHour = sunsetHour;
        this.sunriseDirectionDegree = sunriseDirectionDegree;
        this.directionSweepDegree = directionSweepDegree;
    }

    @Override
    protected void processSystem() {
        float currentHour = timeSource.getHour()
            + timeSource.getMinute() / 60f;
        if (currentHour < sunriseHour || currentHour >= sunsetHour) {
            topDownShadowSystem.setSunVisibility(0f);
            return;
        }

        float daylightProgress = (currentHour - sunriseHour)
            / (sunsetHour - sunriseHour);
        float direction = sunriseDirectionDegree
            + directionSweepDegree * daylightProgress;
        float visibility = MathUtils.sin(MathUtils.PI * daylightProgress);
        topDownShadowSystem.getSunLight().setDirection(direction);
        topDownShadowSystem.setSunVisibility(visibility);
    }
}
