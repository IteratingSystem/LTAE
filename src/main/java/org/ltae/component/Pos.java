package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 位置组件
 **/
public class Pos extends SerializeComponent {
    @SerializeParam
    public float x;
    @SerializeParam
    public float y;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);

        set((float)mapObject.getProperties().get("x"), (float)mapObject.getProperties().get("y"));
    }

    /**
     * 返回与另一个Pos的距离
     * @param orderPos
     * @return
     */
    public float dst(Pos orderPos){
        return Vector2.dst(x,y,orderPos.x,orderPos.y);
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void copy(Pos orderPos){
        this.x = orderPos.x;
        this.y = orderPos.y;
    }
}
