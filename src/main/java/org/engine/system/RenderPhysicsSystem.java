package org.engine.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 * @Auther WenLong
 * @Date 2025/2/12 15:54
 * @Description 渲染物理系统(debug)
 **/
public class RenderPhysicsSystem extends BaseSystem {
    private boolean debug;
    private Box2DDebugRenderer debugRenderer;

    private B2dSystem b2DSystem;
    private CameraSystem cameraSystem;
    public RenderPhysicsSystem(boolean debug){
        this.debug = debug;
    }

    @Override
    protected void initialize() {

        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    protected void processSystem() {

        if (debug){
            debugRenderer.render(b2DSystem.box2DWorld,cameraSystem.camera.combined);
        }
    }
}
