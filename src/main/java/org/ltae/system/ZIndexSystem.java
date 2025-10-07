package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.Pos;
import org.ltae.component.ZIndex;

/**
 * @Auther WenLong
 * @Date 2025/6/25 10:26
 * @Description ZIndex跟随角色坐标就行改变
 **/
@All({ZIndex.class, Pos.class})
public class ZIndexSystem extends IteratingSystem {
    private M<Pos> mPos;
    private M<ZIndex> mZIndex;
    private TiledMapSystem tiledMapSystem;
    @Override
    protected void process(int entityId) {
        ZIndex zIndex = mZIndex.get(entityId);
        if (!zIndex.followY) {
            return;
        }
        TiledMapTileLayer mapLayer = (TiledMapTileLayer)tiledMapSystem.getTiledMap().getLayers().get(0);
        int height = mapLayer.getTileHeight() * mapLayer.getHeight();
        Pos pos = mPos.get(entityId);
        zIndex.index = height - (pos.y+zIndex.offset);
    }
}
