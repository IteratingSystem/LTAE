package org.engine.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
public class CameraSystem extends BaseSystem {
    private final static float MOVE_SPEED = 5;
    public OrthographicCamera camera;

    private float worldScale;
    private float windowWidth;
    private float windowHeight;
    private float zoom;
    private boolean debug;
    public CameraSystem(float windowWidth,float windowHeight,float worldScale,float zoom,boolean debug){
        this.zoom = zoom;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.worldScale = worldScale;
        this.debug =debug;
    }
    @Override
    protected void initialize() {
        camera = new OrthographicCamera();
//        camera.setToOrtho(false, worldScale * SystemConstants.winWidth /4f,worldScale * SystemConstants.winHeight/4f);
        camera.setToOrtho(false, worldScale * windowWidth / zoom,worldScale * windowHeight / zoom);
    }

    @Override
    protected void processSystem() {
        cameraCtrl();
        camera.update();
    }

    /**
     * 在开发过程中,如果debug=true,则可以通过上下左右来移动摄像头
     */
    private void cameraCtrl(){
        if (!debug){
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            camera.position.x = camera.position.x+MOVE_SPEED;
        }else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            camera.position.x = camera.position.x-MOVE_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            camera.position.y = camera.position.y+MOVE_SPEED;
        }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            camera.position.y = camera.position.y-MOVE_SPEED;
        }
    }
}
