package org.ltae.system;

import box2dLight.RayHandler;
import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;

/**
 * @author: LightSystem
 * @date: 2026/1/14
 * @description: 光线系统, 使用box2dLight
 */

public class LightSystem extends BaseSystem {

    private CameraSystem cameraSystem;
    private B2dSystem b2dSystem;

    public RayHandler rayHandler;
    private boolean enable;
    public LightSystem(boolean enable){
        this.enable = enable;
    }
    @Override
    protected void initialize() {
        super.initialize();

        if(!enable){
            return;
        }
        //光线相关
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(b2dSystem.box2DWorld);
        rayHandler.setBlurNum(3);
        Color color = Color.WHITE;
        color.r = 0.5f;
        color.g = 0.5f;
        color.b = 0.5f;
        rayHandler.setAmbientLight(color);
    }

    @Override
    protected void processSystem() {
        if(!enable){
            return;
        }

        if (rayHandler == null) {
            return;
        }
        rayHandler.setCombinedMatrix(cameraSystem.camera);
        rayHandler.updateAndRender();
    }
    public void setAmbientLight(Color color){
        if(!enable){
            return;
        }

        rayHandler.setAmbientLight(color);
    }
    @Override
    protected void dispose() {
        super.dispose();
        if (rayHandler != null) {
            rayHandler.dispose();
        }
    }


}
