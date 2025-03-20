package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.camera.FollowTarget;
import org.ltae.component.Pos;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
public class CameraSystem extends BaseSystem {
    private final static String TAG = CameraSystem.class.getSimpleName();
    private final static float MOVE_SPEED = 5;
    public OrthographicCamera camera;
    public M<Pos> mPos;

    private FollowTarget followTarget;
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
        camera = new OrthographicCamera();
//        camera.setToOrtho(false, worldScale * SystemConstants.winWidth /4f,worldScale * SystemConstants.winHeight/4f);
        camera.setToOrtho(false, worldScale * windowWidth / zoom,worldScale * windowHeight / zoom);
    }

    @Override
    protected void processSystem() {
        followTarget();
        cameraCtrl();
        camera.update();
    }

    private void followTarget() {
        if (followTarget == null) {
            return;
        }


        if (followTarget.entityTag.isEmpty()) {
            Gdx.app.log(TAG, "followTarget has no entity set!");
            return;
        }

        int followingId = world.getSystem(TagManager.class).getEntityId(followTarget.entityTag);
        if (followingId == -1){
            Gdx.app.error(TAG, "TagManager not has this tag:"+followTarget.entityTag);
            return;
        }
        if (!mPos.has(followingId)) {
            Gdx.app.log(TAG, "The following entity does not have a Pos component!");
            return;
        }

        Pos pos = mPos.get(followingId);
        float centerX = pos.x + followTarget.eCenterX;
        float centerY = pos.y + followTarget.eCenterY;

        float activeWidth = followTarget.activeWidth;
        float activeHeight = followTarget.activeHeight;

        // 平滑过渡到目标位置

        // 如果目标实体超出摄像机的活动区域，则调整摄像机位置
        if (camera.position.x < centerX - activeWidth / 2 || camera.position.x > centerX + activeWidth / 2) {
            camera.position.x = MathUtils.lerp(camera.position.x, centerX, followTarget.progress); // 平滑过渡
        }
        if (camera.position.y < centerY - activeHeight / 2 || camera.position.y > centerY + activeHeight / 2) {
            camera.position.y = MathUtils.lerp(camera.position.y, centerY, followTarget.progress); // 平滑过渡
        }
    }
    public void setFollowTarget(FollowTarget followTarget){
        this.followTarget = followTarget;
    }

    /**
     * 在开发过程中,如果日志等级为DEBUG,则可以通过上下左右来移动摄像头
     */
    private void cameraCtrl(){
        if (Gdx.app.getLogLevel() != Logger.DEBUG){
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
