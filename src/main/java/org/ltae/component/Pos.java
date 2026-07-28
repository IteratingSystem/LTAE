package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
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
    public void write(Json json) {
        super.write(json);
        json.writeValue("x", x);
        json.writeValue("y", y);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        x = jsonData.getFloat("x", 0f);
        y = jsonData.getFloat("y", 0f);
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
