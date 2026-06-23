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
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.SerializeSystem;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
@SerializeSystem
public class CameraSystem extends BaseSystem {
    private final static String TAG = CameraSystem.class.getSimpleName();
    private final static float MOVE_SPEED = 5;
    public M<Pos> mPos;

    public OrthographicCamera camera;
    @SerializeParam
    public CameraTarget cameraTarget;
    private float worldScale;
    private float gameWidth;
    private float gameHeight;
    private float zoom;
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
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG){
            cameraCtrl();
        }
        followTarget();
        camera.update();
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
        if (!verifyTarget()) {
            return;
        }

        int followingId = world.getSystem(TagManager.class).getEntityId(cameraTarget.entityTag);
        Pos pos = mPos.get(followingId);
        float centerX = pos.x + cameraTarget.eCenterX;
        float centerY = pos.y + cameraTarget.eCenterY;

        float activeWidth = cameraTarget.activeWidth;
        float activeHeight = cameraTarget.activeHeight;

        // 检查是否超出活动区域
        boolean outX = camera.position.x < centerX - activeWidth / 2 + cameraTarget.offsetX ||
                camera.position.x > centerX + activeWidth / 2 + cameraTarget.offsetX;
        boolean outY = camera.position.y < centerY - activeHeight / 2 + cameraTarget.offsetY ||
                camera.position.y > centerY + activeHeight / 2 + cameraTarget.offsetY;

        // 如果未超出，则无需移动
        if (!outX && !outY) {
            return;
        }

        // 计算当前偏差
        float dx = centerX - camera.position.x;
        float dy = centerY - camera.position.y;
        final float THRESHOLD = 0.01f;  // 像素阈值，可微调

        // 偏差极小则直接吸附，彻底消除残余抖动
        if (Math.abs(dx) < THRESHOLD && Math.abs(dy) < THRESHOLD) {
            camera.position.x = centerX;
            camera.position.y = centerY;
            return;
        }

        // 指数平滑参数（decay 越大，跟随越快）
        // 这里使用 cameraTarget.progress 映射到 decay，例如 progress 0~1 对应 decay 2~10
        float decay = 2.0f + cameraTarget.progress * 8.0f; // 可根据需要调整范围
        float delta = world.getDelta();  // 当前帧间隔（秒）
        float smoothFactor = 1f - (float) Math.exp(-decay * delta);

        // 分别对超出方向进行平滑插值
        if (outX) {
            camera.position.x = MathUtils.lerp(camera.position.x, centerX, smoothFactor);
        }
        if (outY) {
            camera.position.y = MathUtils.lerp(camera.position.y, centerY, smoothFactor);
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

    private void resize(float width, float height) {
        float zoom = LtaePluginRule.CAMERA_ZOOM * width / LtaePluginRule.GAME_WIDTH;
        camera.viewportWidth = width/zoom;
        camera.viewportHeight = height/zoom;
        camera.update();
    }
    private void updateZoom(float zoom){
        this.zoom = zoom;
        LtaePluginRule.CAMERA_ZOOM = zoom;
        resize(gameWidth,gameHeight);
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
        if (event.type == CameraEvent.UPDATE_ZOOM) {
            updateZoom(event.zoom);
            return;
        }

    }
}
