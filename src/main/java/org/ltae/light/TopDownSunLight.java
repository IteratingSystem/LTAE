package org.ltae.light;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * 只产生俯视角阴影、不向LightMap发光的太阳光。
 */
public final class TopDownSunLight extends DirectionalLight
    implements TopDownShadowLight {
    private final Vector2 shadowDirection = new Vector2();
    private final float shadowHeight;

    public TopDownSunLight(RayHandler rayHandler, float directionDegree,
                           float shadowHeight) {
        super(rayHandler, 3, new Color(0f, 0f, 0f, 0f), directionDegree);
        this.shadowHeight = shadowHeight;
        setXray(true);
        setSoft(false);
        setStaticLight(true);
        setDirection(directionDegree);
        setActive(false);
    }

    @Override
    public void setDirection(float directionDegree) {
        super.setDirection(directionDegree);
        // 父类构造期间会调用此方法，此时子类字段尚未初始化。
        if (shadowDirection != null) {
            shadowDirection.set(
                MathUtils.cosDeg(directionDegree),
                MathUtils.sinDeg(directionDegree));
        }
    }

    @Override
    public boolean isDirectional() {
        return true;
    }

    @Override
    public float getShadowHeight() {
        return shadowHeight;
    }

    @Override
    public float getShadowRange() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public Vector2 getShadowDirection(Vector2 out) {
        return out.set(shadowDirection);
    }

    @Override
    public Vector2 getShadowPosition(Vector2 out) {
        return out.setZero();
    }
}
