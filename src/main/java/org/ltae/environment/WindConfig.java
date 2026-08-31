package org.ltae.environment;

import com.badlogic.gdx.math.Vector2;

/**
 * 世界风向与风速初始配置。
 */
public final class WindConfig {
    private final Vector2 direction = new Vector2(1f, 1f).nor();
    private float speed = 1.41421356f;

    public Vector2 getDirection(Vector2 out) {
        return out.set(direction);
    }

    public WindConfig setDirection(float x, float y) {
        if (x == 0f && y == 0f) {
            throw new IllegalArgumentException("wind direction cannot be zero");
        }
        direction.set(x, y).nor();
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public WindConfig setSpeed(float speed) {
        if (speed < 0f) {
            throw new IllegalArgumentException("wind speed cannot be negative");
        }
        this.speed = speed;
        return this;
    }
}
