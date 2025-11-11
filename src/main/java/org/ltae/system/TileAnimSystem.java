package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
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
    private final static String TAG = TileAnimSystem.class.getSimpleName();
    private M<TileAnimations> mTileAnimations;
    private M<TileAnimation> mTileAnimation;
    private M<Render> mRender;
    @Override
    protected void process(int entityId) {
        Render render = mRender.get(entityId);
        TileAnimation tileAnimation;
        if (mTileAnimations.has(entityId)) {
            TileAnimations tileAnimations = mTileAnimations.get(entityId);
            tileAnimation = tileAnimations.getTileAnimation();
            if (tileAnimation == null){
                Gdx.app.error(TAG,"The current animation does not exist in tileAnimations: "+tileAnimations.current+",Please confirm the animation name: "+tileAnimations.current);
                return;
            }
        }else {
            tileAnimation = mTileAnimation.get(entityId);
        }
        if (tileAnimation.getKeyFrame() == null) {
            return;
        }
        tileAnimation.stateTime += world.getDelta();
        render.keyframe = tileAnimation.getKeyFrame();
        render.offsetX = tileAnimation.offsetX;
        render.offsetY = tileAnimation.offsetY;
    }
}
