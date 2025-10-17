package org.ltae.system;

import com.artemis.BaseSystem;

public class TotalTimeSystem extends BaseSystem {
    public float totalTime;

    @Override
    protected void initialize() {
        super.initialize();
        totalTime = 0f;
    }

    @Override
    protected void processSystem() {
        totalTime += world.getDelta();
    }
}
