package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Pos extends Component implements ComponentLoader {
    public float x;
    public float y;

    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        MapObject mapObject = entityDetails.mapObject;

        x = (float)mapObject.getProperties().get("x");
        y = (float)mapObject.getProperties().get("y");
    }

    /**
     * 返回与另一个Pos的距离
     * @param orderPos
     * @return
     */
    public float dst(Pos orderPos){
        return Vector2.dst(x,y,orderPos.x,orderPos.y);
    }
}
