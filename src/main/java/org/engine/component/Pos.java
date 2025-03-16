package org.engine.component;

import com.artemis.Component;
import com.badlogic.gdx.maps.MapObject;
import org.engine.tiled.TileCompLoader;
import org.engine.tiled.TileDetails;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Pos extends Component implements TileCompLoader {
    public float x;
    public float y;

    @Override
    public void loader(TileDetails tileDetails) {
        MapObject mapObject = tileDetails.mapObject;

        x = (float)mapObject.getProperties().get("x");
        y = (float)mapObject.getProperties().get("y");
    }
}
