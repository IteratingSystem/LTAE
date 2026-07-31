package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.PostLoad;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Render extends SerializeComponent {
    @SerializeParam
    public boolean visible;

    public float offsetX = 0;
    public float offsetY = 0;
    public float scaleWidth = 1;
    public float scaleHeight = 1;
    public transient TextureRegion keyframe;

    //纹理集,用于堆叠渲染
    public transient Array<TextureRegion> textureSheets;
    public float sheetOffset;

    public boolean flipX = false;
    public boolean flipY = false;
    //旋转中心与旋转角度
    public float originX;
    public float originY;
    public float rotation;

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("visible", visible);
        json.writeValue("offsetX", offsetX);
        json.writeValue("offsetY", offsetY);
        json.writeValue("scaleWidth", scaleWidth);
        json.writeValue("scaleHeight", scaleHeight);
        json.writeValue("sheetOffset", sheetOffset);
        json.writeValue("flipX", flipX);
        json.writeValue("flipY", flipY);
        json.writeValue("originX", originX);
        json.writeValue("originY", originY);
        json.writeValue("rotation", rotation);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        visible = jsonData.getBoolean("visible", false);
        offsetX = jsonData.getFloat("offsetX", 0f);
        offsetY = jsonData.getFloat("offsetY", 0f);
        scaleWidth = jsonData.getFloat("scaleWidth", 1f);
        scaleHeight = jsonData.getFloat("scaleHeight", 1f);
        sheetOffset = jsonData.getFloat("sheetOffset", 0f);
        flipX = jsonData.getBoolean("flipX", false);
        flipY = jsonData.getBoolean("flipY", false);
        originX = jsonData.getFloat("originX", 0f);
        originY = jsonData.getFloat("originY", 0f);
        rotation = jsonData.getFloat("rotation", 0f);
    }

    @PostLoad
    public void postLoadRender(World world) {
        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyframe = textureMapObject.getTextureRegion();
        }
    }
}
