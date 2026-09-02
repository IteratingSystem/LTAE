package org.worldloom.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

/** 在世界效果完成后恢复逻辑摄像机，并把世界缓冲合成到屏幕。 */
public final class PixelPerfectCompositeSystem extends BaseSystem {
    private final PixelPerfectRenderSystem renderSystem;
    private final Matrix4 projection = new Matrix4();
    private SpriteBatch batch;

    public PixelPerfectCompositeSystem(PixelPerfectRenderSystem renderSystem) {
        this.renderSystem = renderSystem;
    }

    @Override
    protected void initialize() {
        if (renderSystem.isEnabled()) {
            batch = new SpriteBatch();
            batch.disableBlending();
        }
    }

    @Override
    protected void processSystem() {
        if (!renderSystem.isEnabled()) {
            return;
        }
        renderSystem.finishWorldTarget();
        int width = Math.max(1, Gdx.graphics.getBackBufferWidth());
        int height = Math.max(1, Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glViewport(0, 0, width, height);
        projection.setToOrtho2D(0f, 0f, width, height);
        batch.setProjectionMatrix(projection);
        float x = -renderSystem.getPadding()
            - renderSystem.getScreenOffsetX();
        float y = -renderSystem.getPadding()
            - renderSystem.getScreenOffsetY();
        batch.begin();
        batch.draw(renderSystem.getWorldRegion(), x, y,
            renderSystem.getRenderWidth(), renderSystem.getRenderHeight());
        batch.end();
    }

    @Override
    protected void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}
