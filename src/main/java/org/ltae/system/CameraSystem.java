package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.LtaePluginRule;
import org.ltae.camera.CameraTarget;
import org.ltae.component.Pos;
import org.ltae.event.CameraEvent;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
public class CameraSystem extends BaseSystem {
    private final static String TAG = CameraSystem.class.getSimpleName();
    private TagManager tagManager;

    private final static float MOVE_SPEED = 5;
    public M<Pos> mPos;

    public OrthographicCamera camera;
    private CameraTarget cameraTarget;
    private float worldScale;
    private float gameWidth;
    private float gameHeight;
    private float zoom;

    private int targetId = -1;   // 缓存帧尾最终 entityId
    private Pos targetPos;       // 缓存帧尾最终 Pos
    private boolean dirty = false;
    public CameraSystem(float gameWidth,float gameHeight,float zoom,float worldScale){
        this.zoom = zoom;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.worldScale = worldScale;
    }
    @Override
    protected void initialize() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldScale * gameWidth/zoom,worldScale * gameHeight/zoom);
//        camera.zoom = zoom;
//        camera.update();
    }

    @Override
    protected void processSystem() {
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG) {
            cameraCtrl();        // 调试手操
        }
        // 采样：只记录“本帧最终位置”，不立即写 camera.position
        if (verifyTarget()) {
            targetId = tagManager.getEntityId(cameraTarget.entityTag);
            targetPos  = mPos.get(targetId);
            dirty = true;
        } else {
            dirty = false;
        }
    }
    @Override
    protected void end() {
        if (!dirty) {            // 本帧没有目标，直接返回
            super.end();
            return;
        }
        // 1. 先按活动区得到“小数目标”
        float centerX = targetPos.x + cameraTarget.eCenterX;
        float centerY = targetPos.y + cameraTarget.eCenterY;

        float aw = cameraTarget.activeWidth;
        float ah = cameraTarget.activeHeight;

        float targetCamX = camera.position.x;   // 默认保持原位
        float targetCamY = camera.position.y;

        // 水平方向超出活动区 → 需要修正
        if (camera.position.x < centerX - aw/2 + cameraTarget.offsetX ||
                camera.position.x > centerX + aw/2 + cameraTarget.offsetX) {
            targetCamX = centerX;
        }
        // 垂直方向超出活动区 → 需要修正
        if (camera.position.y < centerY - ah/2 + cameraTarget.offsetY ||
                camera.position.y > centerY + ah/2 + cameraTarget.offsetY) {
            targetCamY = centerY;
        }

        // 2. 把目标摄像机位置对齐到世界像素网格
        float pixel = 1f / worldScale;
        targetCamX = Math.round(targetCamX / pixel) * pixel;
        targetCamY = Math.round(targetCamY / pixel) * pixel;

        // 3. 用 lerp 朝“整格目标”插值（角色仍在活动区内时 targetCam*==camera.position*，lerp 不变）
        camera.position.x = MathUtils.lerp(camera.position.x, targetCamX, cameraTarget.progress);
        camera.position.y = MathUtils.lerp(camera.position.y, targetCamY, cameraTarget.progress);

        // 4. 最后统一 round 一次，防止 lerp 残差
        camera.position.x = Math.round(camera.position.x / pixel) * pixel;
        camera.position.y = Math.round(camera.position.y / pixel) * pixel;

        camera.update();
        dirty = false;
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
    private void jumpToPos(Pos pos){
        camera.position.x = pos.x;
        camera.position.y = pos.y;
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

    /**
     * 设置跟随目标
     * @param cameraTarget
     */
    private void setTarget(CameraTarget cameraTarget){
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

    private void resize(int width, int height) {
        float zoom = LtaePluginRule.CAMERA_ZOOM * width / LtaePluginRule.GAME_WIDTH;
        camera.viewportWidth = width/zoom;
        camera.viewportHeight = height/zoom;
        camera.update();
    }

    @Subscribe
    public void onEvent(CameraEvent event){
        if (event.type == CameraEvent.SET_TARGET) {
            setTarget(event.target);
            return;
        }
        if (event.type == CameraEvent.RESIZE) {
            resize(event.width,event.height);
            return;
        }
        if (event.type == CameraEvent.JUMP_POS) {
            jumpToPos(event.pos);
            return;
        }

    }
}
