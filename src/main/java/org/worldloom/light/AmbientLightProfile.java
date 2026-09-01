package org.worldloom.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * 一张地图使用的环境光配置，可以是逐小时变化的曲线，也可以是恒定颜色。
 */
public final class AmbientLightProfile {
    public static final int HOURS_PER_DAY = 24;

    private final Color[] hourlyColors;

    private AmbientLightProfile(Color[] hourlyColors) {
        if (hourlyColors == null || hourlyColors.length != HOURS_PER_DAY) {
            throw new IllegalArgumentException("hourlyColors must contain exactly 24 colors");
        }
        this.hourlyColors = new Color[HOURS_PER_DAY];
        for (int i = 0; i < HOURS_PER_DAY; i++) {
            if (hourlyColors[i] == null) {
                throw new IllegalArgumentException("hourlyColors cannot contain null");
            }
            this.hourlyColors[i] = new Color(hourlyColors[i]);
        }
    }

    public static AmbientLightProfile hourly(Color... hourlyColors) {
        return new AmbientLightProfile(hourlyColors);
    }

    public static AmbientLightProfile constant(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null");
        }
        Color[] hourlyColors = new Color[HOURS_PER_DAY];
        for (int i = 0; i < HOURS_PER_DAY; i++) {
            hourlyColors[i] = color;
        }
        return new AmbientLightProfile(hourlyColors);
    }

    /**
     * 获取指定时间的环境光颜色，结果写入 output，避免每帧创建对象。
     */
    public Color sample(int hour, int minute, Color output) {
        if (output == null) {
            throw new IllegalArgumentException("output cannot be null");
        }
        int currentHour = Math.floorMod(hour, HOURS_PER_DAY);
        int nextHour = (currentHour + 1) % HOURS_PER_DAY;
        float progress = MathUtils.clamp(minute, 0, 59) / 60f;
        return output.set(hourlyColors[currentHour]).lerp(hourlyColors[nextHour], progress);
    }
}
