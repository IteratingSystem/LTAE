package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * @Auther WenLong
 * @Date 2025/4/23 16:03
 * @Description debug中的形状
 **/
public class RenderShapeSystem extends BaseSystem {
    private CameraSystem cameraSystem;
    private ShapeRenderer shapeRenderer;

    @Override
    protected void initialize() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    protected void processSystem() {
        if (Gdx.app.getLogLevel() != Application.LOG_DEBUG){
            return;
        }

        shapeRenderer.setTransformMatrix(cameraSystem.camera.combined);
        shapeRenderer.begin();

        cameraSystem.renderTarget(shapeRenderer);

        shapeRenderer.end();
    }
}
