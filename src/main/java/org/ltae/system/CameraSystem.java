package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.camera.CameraTarget;
import org.ltae.component.Pos;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
public class CameraSystem extends BaseSystem {
    private final static String TAG = CameraSystem.class.getSimpleName();
    private final static float MOVE_SPEED = 5;
    private  FitViewport viewport;
    public OrthographicCamera camera;
    public M<Pos> mPos;

    private CameraTarget cameraTarget;
    private float worldScale;
    private float windowWidth;
    private float windowHeight;
    private float zoom;
    public CameraSystem(float windowWidth,float windowHeight,float zoom,float worldScale){
        this.zoom = zoom;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.worldScale = worldScale;
    }
    @Override
    protected void initialize() {
        viewport = new FitViewport(worldScale * windowWidth / zoom,worldScale * windowHeight / zoom);
        camera = (OrthographicCamera)viewport.getCamera();
        camera.setToOrtho(false);
    }

    @Override
    protected void processSystem() {
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG){
            cameraCtrl();
        }
        followTarget();
        camera.update();
    }

    public void resize(int width,int height){
        viewport.update(width,height);
        camera = (OrthographicCamera) viewport.getCamera();
    }
    private boolean verifyTarget(){
        if (cameraTarget == null) {
            return false;
        }
        if (cameraTarget.entityTag.isEmpty()) {
            Gdx.app.log(TAG, "FollowTarget has no entity set!");
            return false;
        }
        if (!world.getSystem(TagManager.class).isRegistered(cameraTarget.entityTag)){
            Gdx.app.error(TAG, "TagManager is not registered tag:"+ cameraTarget.entityTag);
            return false;
        }

        int followingId = world.getSystem(TagManager.class).getEntityId(cameraTarget.entityTag);
        if (!mPos.has(followingId)) {
            Gdx.app.log(TAG, "The following entity does not have a Pos component!");
            return false;
        }
        return true;
    }
    private void followTarget() {
        if (!verifyTarget()){
            return;
        }

        int followingId = world.getSystem(TagManager.class).getEntityId(cameraTarget.entityTag);
        Pos pos = mPos.get(followingId);
        float centerX = pos.x + cameraTarget.eCenterX;
        float centerY = pos.y + cameraTarget.eCenterY;

        float activeWidth = cameraTarget.activeWidth;
        float activeHeight = cameraTarget.activeHeight;

        // 如果目标实体超出摄像机的活动区域，则调整摄像机位置,平滑过渡到目标位置
        if (camera.position.x < centerX - activeWidth / 2 + cameraTarget.offsetX || camera.position.x > centerX + activeWidth / 2 + cameraTarget.offsetX) {
            camera.position.x = MathUtils.lerp(camera.position.x, centerX, cameraTarget.progress); // 平滑过渡
        }
        if (camera.position.y < centerY - activeHeight / 2 + cameraTarget.offsetY || camera.position.y > centerY + activeHeight / 2 + cameraTarget.offsetY) {
            camera.position.y = MathUtils.lerp(camera.position.y, centerY, cameraTarget.progress); // 平滑过渡
        }
    }

    public void setFollowTarget(CameraTarget cameraTarget){
        this.cameraTarget = cameraTarget;
    }

    /**
     * 在开发过程中,如果日志等级为DEBUG,则可以通过上下左右来移动摄像头
     */
    private void cameraCtrl(){
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
