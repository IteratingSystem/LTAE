package org.worldloom.light;

import com.badlogic.gdx.math.Vector2;

/**
 * 太阳光与点光源共用的俯视角阴影参数。
 */
public interface TopDownShadowLight {
    boolean isDirectional();

    float getShadowHeight();

    float getShadowRange();

    default float getShadowLengthScale() {
        return 1f;
    }

    Vector2 getShadowDirection(Vector2 out);

    Vector2 getShadowPosition(Vector2 out);
}
