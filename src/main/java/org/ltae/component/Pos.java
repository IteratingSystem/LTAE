package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityDatum;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 位置组件
 **/
public class Pos extends SerializeComponent{
    @SerializeParam
    public float x = -1;
    @SerializeParam
    public float y = -1;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        if (x == -1 && y == -1){
            x = (float)mapObject.getProperties().get("x");
            y = (float)mapObject.getProperties().get("y");
        }
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
