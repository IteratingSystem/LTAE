package org.worldloom.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.worldloom.component.Render;
import org.worldloom.component.Slice;
import org.worldloom.component.TileAnimation;

/**
 * 从动画帧建立逐层切片精灵。
 */
@All({Render.class, TileAnimation.class, Slice.class})
public class SliceSystem extends IteratingSystem {
    private M<TileAnimation> mTileAnimation;
    private M<Render> mRender;

    @Override
    protected void process(int entityId) {
        TileAnimation tileAnimation = mTileAnimation.get(entityId);
        tileAnimation.isPause = true;

        Render render = mRender.get(entityId);
        if (render.textureSheets == null) {
            render.textureSheets = new Array<>();
            render.textureSheets.addAll(tileAnimation.getkeyframes());
            render.textureSheets.reverse();
            render.sheetOffset = 0.75f;
        }
    }
}
