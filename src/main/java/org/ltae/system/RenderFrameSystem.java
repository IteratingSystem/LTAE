package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
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
        if (!render.visible || render.keyFrame.getTexture() == null) {
            return;
        }


        //渲染
        Pos pos = mPos.get(entityId);

        batch.begin();

        TextureRegion keyFrame = render.keyFrame;

        batch.draw(keyFrame,
            worldScale*(pos.x +render.offsetX),worldScale*(pos.y + render.offsetY),
            0,0,
            keyFrame.getRegionWidth(),keyFrame.getRegionHeight(),
            worldScale*render.scaleW,worldScale*render.scaleH,
            0
        );


        batch.end();
    }
}
