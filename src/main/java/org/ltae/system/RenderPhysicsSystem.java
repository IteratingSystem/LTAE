package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Logger;
import org.ltae.component.singleton.Camera;

/**
 * @Auther WenLong
 * @Date 2025/2/12 15:54
 * @Description 渲染物理系统(debug)
 **/
public class RenderPhysicsSystem extends BaseSystem {
    private Box2DDebugRenderer debugRenderer;
    private Camera camera;
    private B2dSystem b2DSystem;
    public RenderPhysicsSystem(){

    }

    @Override
    protected void initialize() {

        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    protected void processSystem() {
        if (Gdx.app.getLogLevel() == Logger.DEBUG){
            debugRenderer.render(b2DSystem.box2DWorld,camera.worldCamera.combined);
        }
    }
}
