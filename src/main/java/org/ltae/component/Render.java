package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.TiledMapUtils;

import java.util.Iterator;

/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Render extends Component implements ComponentLoader {
    public boolean visible = true;
    public float offsetX = 0;
    public float offsetY = 0;
    public float scaleWidth = 1;
    public float scaleHeight = 1;
    public TextureRegion keyframe;

    public boolean flipX = false;
    public boolean flipY = false;


    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        MapObject mapObject = entityDetails.mapObject;
        MapProperties properties = mapObject.getProperties();
        String name = mapObject.getName();
        if ("enemyLaser".equals(name)){
            TiledMapUtils.logProperties(properties);
        }

        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyframe = textureMapObject.getTextureRegion();
            flipX = textureMapObject.getTextureRegion().isFlipX();
            flipY = textureMapObject.getTextureRegion().isFlipY();
            int regionWidth = keyframe.getRegionWidth();
            int regionHeight = keyframe.getRegionHeight();
            float tileWidth = properties.get("width",(float)regionWidth, float.class);
            float tileHeight = properties.get("height",(float)regionHeight, float.class);
            scaleWidth = tileWidth/regionWidth;
            scaleHeight = tileHeight/regionHeight;
        }

    }
}
