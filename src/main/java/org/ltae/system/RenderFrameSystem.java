package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import org.ltae.component.Pos;
import org.ltae.component.Render;

/**
 * @Auther WenLong
 * @Date 2024/6/6 9:45
 * @Description 渲染region帧对象
 **/
@Wire
@All({Render.class, Pos.class})
public class RenderFrameSystem extends DeferredEntityProcessingSystem {
    private RenderTiledSystem renderTiledSystem;
    private CameraSystem cameraSystem;

    private M<Render> mRender;
    private M<Pos> mPos;

    private Batch batch;
    private float worldScale;
    public RenderFrameSystem(EntityProcessPrincipal principal,float worldScale) {
        super(principal);
        this.worldScale = worldScale;
    }
    @Override
    protected void initialize() {
        batch = renderTiledSystem.mapRenderer.getBatch();
    }

    @Override
    protected void process(int entityId) {
        Render render = mRender.get(entityId);
        //判断是否需要显示
        if (render.keyframe == null || render.keyframe.getTexture() == null || !render.visible) {
            return;
        }


        //渲染
        Pos pos = mPos.get(entityId);

        batch.begin();

        TextureRegion keyFrame = render.keyframe;
        int regionWidth = keyFrame.getRegionWidth();
        int regionHeight = keyFrame.getRegionHeight();
        float scaleWidth = render.scaleWidth;
        float scaleHeight = render.scaleHeight;
        batch.draw(keyFrame.getTexture(), // 指定要绘制的纹理对象
                worldScale * (pos.x + render.offsetX), worldScale * (pos.y + render.offsetY), // 指定绘制的起始位置（左下角）
                0, 0, // 指定旋转的中心点（相对于绘制位置的偏移量）
                keyFrame.getRegionWidth(), keyFrame.getRegionHeight(), // 指定目标绘制区域的大小
                worldScale * scaleWidth, worldScale * scaleHeight, // 指定 X 轴和 Y 轴的缩放比例
                0, // 指定旋转角度
                keyFrame.getRegionX(), keyFrame.getRegionY(), // 指定源纹理区域的起始坐标
                regionWidth, regionHeight, // 指定源纹理区域的大小
                render.flipX, // x轴翻转
                render.flipY // y轴翻转
        );
        batch.end();
    }
}
