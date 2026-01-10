package org.ltae.component;

import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import org.ltae.component.parent.SerializeComponent;
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
    public  boolean visible;
    public  float offsetX = 0;
    public  float offsetY = 0;
    public  float scaleWidth = 1;
    public  float scaleHeight = 1;
    public  TextureRegion keyframe;
    public  boolean flipX = false;
    public  boolean flipY = false;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyframe = textureMapObject.getTextureRegion();
            flipX = textureMapObject.getTextureRegion().isFlipX();
            flipY = textureMapObject.getTextureRegion().isFlipY();
            if (entityDatum.dataFrom == EntityData.FROM_MAP){
                visible = textureMapObject.isVisible();
            }
            int regionWidth = keyframe.getRegionWidth();
            int regionHeight = keyframe.getRegionHeight();
            MapProperties properties = mapObject.getProperties();
            float tileWidth = properties.get("width",(float)regionWidth, float.class);
            float tileHeight = properties.get("height",(float)regionHeight, float.class);
            scaleWidth = tileWidth/regionWidth;
            scaleHeight = tileHeight/regionHeight;
        }
    }
}
