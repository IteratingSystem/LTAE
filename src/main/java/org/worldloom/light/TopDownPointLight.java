package org.worldloom.light;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * 使用box2dlights发光，并产生俯视角精灵阴影的点光源。
 */
public final class TopDownPointLight extends PointLight
    implements TopDownShadowLight {
    private final float shadowHeight;

    public TopDownPointLight(RayHandler rayHandler, int rays, Color color,
                             float distance, float x, float y,
                             float shadowHeight) {
        super(rayHandler, rays, color, distance, x, y);
        this.shadowHeight = shadowHeight;
        setXray(true);
        setSoft(false);
    }

    @Override
    public boolean isDirectional() {
        return false;
    }

    @Override
    public float getShadowHeight() {
        return shadowHeight;
    }

    @Override
    public float getShadowRange() {
        return getDistance();
    }

    @Override
    public Vector2 getShadowDirection(Vector2 out) {
        return out.setZero();
    }

    @Override
    public Vector2 getShadowPosition(Vector2 out) {
        return out.set(getX(), getY());
    }
}
