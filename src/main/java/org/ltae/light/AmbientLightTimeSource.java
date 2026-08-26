package org.ltae.light;

/**
 * 环境光所需的游戏时间来源。
 */
public interface AmbientLightTimeSource {
    int getHour();

    int getMinute();
}
