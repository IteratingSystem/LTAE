package org.worldloom.system;

import com.artemis.BaseSystem;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.worldloom.camera.CameraTarget;
import org.worldloom.component.Pos;
import org.worldloom.event.CameraEvent;
import org.worldloom.serialize.SerializeParam;
import org.worldloom.serialize.SerializeSystem;

/**
 * @Author: WenLong
 * @Date: 2024-09-09-17:48
 * @Description: 摄像机系统
 */
@SerializeSystem
public class CameraSystem extends BaseSystem {
    private final static String TAG = CameraSystem.class.getSimpleName();
    private final static float MOVE_SPEED = 5;
    private final static float POSITION_EPSILON = 0.0001f;
    public M<Pos> mPos;

    public OrthographicCamera camera;
    @SerializeParam
    public CameraTarget cameraTarget;
    private float worldScale;
    private float gameWidth;
    private float gameHeight;
    private float zoom;
    private float logicalX;
    private float logicalY;
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
        logicalX = camera.position.x;
        logicalY = camera.position.y;
//        camera.zoom = zoom;
//        camera.update();
    }

    @Override
    protected void processSystem() {
        if (Gdx.app.getLogLevel() == Application.LOG_DEBUG){
            cameraCtrl();
        }
        followTarget();
        applyLogicalPosition();
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
        logicalX = pos.x;
        logicalY = pos.y;
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

        float halfWidth = Math.max(0f, activeWidth) * 0.5f;
        float halfHeight = Math.max(0f, activeHeight) * 0.5f;
        float minX = centerX + cameraTarget.offsetX - halfWidth;
        float maxX = centerX + cameraTarget.offsetX + halfWidth;
        float minY = centerY + cameraTarget.offsetY - halfHeight;
        float maxY = centerY + cameraTarget.offsetY + halfHeight;

        // 目标取安全区内离当前相机最近的点，避免越界后重新追向实体中心。
        float targetX = MathUtils.clamp(logicalX, minX, maxX);
        float targetY = MathUtils.clamp(logicalY, minY, maxY);
        float dx = targetX - logicalX;
        float dy = targetY - logicalY;
        boolean outX = Math.abs(dx) > POSITION_EPSILON;
        boolean outY = Math.abs(dy) > POSITION_EPSILON;

        // 如果未超出，则无需移动
        if (!outX && !outY) {
            return;
        }

        // 指数平滑参数
        float decay = 2.0f + cameraTarget.progress * 8.0f;
        float delta = world.getDelta();
        float smoothFactor = 1f - (float) Math.exp(-decay * delta);

        // 分别对超出方向进行平滑插值
        if (outX) {
            logicalX = MathUtils.lerp(logicalX, targetX, smoothFactor);
        }
        if (outY) {
            logicalY = MathUtils.lerp(logicalY, targetY, smoothFactor);
        }

        // 仅消除浮点尾差，不对正在移动的摄像机做像素取整。
        if (Math.abs(targetX - logicalX) <= POSITION_EPSILON) {
            logicalX = targetX;
        }
        if (Math.abs(targetY - logicalY) <= POSITION_EPSILON) {
            logicalY = targetY;
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
            logicalX += MOVE_SPEED;
        }else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            logicalX -= MOVE_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            logicalY += MOVE_SPEED;
        }else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            logicalY -= MOVE_SPEED;
        }
    }

    /** 将连续的逻辑位置直接用于渲染，避免屏幕像素量化造成阶梯移动。 */
    private void applyLogicalPosition() {
        camera.position.x = logicalX;
        camera.position.y = logicalY;
    }

    private void resize(float width, float height) {
        float zoom = this.zoom * width / gameWidth;
        camera.viewportWidth = width/zoom;
        camera.viewportHeight = height/zoom;
        applyLogicalPosition();
        camera.update();
    }
    private void updateZoom(float zoom){
        this.zoom = zoom;
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
