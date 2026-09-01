package org.worldloom.system;

import com.artemis.BaseSystem;

/**
 * 在环境光完成后合成俯视角点光源。
 */
public final class TopDownPointLightRenderSystem extends BaseSystem {
    private TopDownShadowSystem topDownShadowSystem;

    @Override
    protected void processSystem() {
        topDownShadowSystem.renderPointLightsAfterAmbient();
    }
}
