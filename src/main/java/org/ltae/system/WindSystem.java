package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Vector2;
import org.ltae.environment.WindConfig;

/**
 * 维护可被海流、云影等环境效果共享的连续风位移。
 */
public final class WindSystem extends BaseSystem {
    private final Vector2 direction = new Vector2();
    private final Vector2 displacement = new Vector2();
    private float speed;

    public WindSystem(WindConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        config.getDirection(direction);
        speed = config.getSpeed();
    }

    @Override
    protected void processSystem() {
        displacement.mulAdd(direction, speed * world.getDelta());
    }

    public Vector2 getDirection(Vector2 out) {
        return out.set(direction);
    }

    public Vector2 getDisplacement(Vector2 out) {
        return out.set(displacement);
    }

    public float getSpeed() {
        return speed;
    }

    public void setDirection(float x, float y) {
        if (x == 0f && y == 0f) {
            throw new IllegalArgumentException("wind direction cannot be zero");
        }
        direction.set(x, y).nor();
    }

    public void setSpeed(float speed) {
        if (speed < 0f) {
            throw new IllegalArgumentException("wind speed cannot be negative");
        }
        this.speed = speed;
    }
}
