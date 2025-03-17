package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.Render;
import org.ltae.component.TileAnimation;
import org.ltae.component.TileAnimations;

/**
 * @Auther WenLong
 * @Date 2025/3/11 16:11
 * @Description 瓦片动画系统
 **/
@All({TileAnimation.class, Render.class})
public class TileAnimSystem extends IteratingSystem {
    private M<TileAnimations> mTileAnimations;
    private M<TileAnimation> mTileAnimation;
    private M<Render> mRender;
    @Override
    protected void process(int entityId) {
        Render render = mRender.get(entityId);
        TileAnimation tileAnimation = mTileAnimation.get(entityId);
        tileAnimation.stateTime += world.getDelta();

        //有先使用动画表中的动画
        if (mTileAnimations.has(entityId)) {
            TileAnimations tileAnimations = mTileAnimations.get(entityId);
            tileAnimations.stateTime += world.getDelta();
            render.keyFrame = tileAnimations.getKeyFrame();
            return;
        }
        render.keyFrame = tileAnimation.getKeyFrame();
    }
}
