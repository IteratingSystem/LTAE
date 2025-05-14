package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;

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
        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyframe = textureMapObject.getTextureRegion();
            flipX = textureMapObject.getTextureRegion().isFlipX();
            flipY = textureMapObject.getTextureRegion().isFlipY();
            MapProperties properties = mapObject.getProperties();
            int regionWidth = keyframe.getRegionWidth();
            int regionHeight = keyframe.getRegionHeight();
            float tileWidth = properties.get("tilewidth", float.class);
            float tileHeight = properties.get("tileheight", float.class);
            scaleWidth = tileWidth/regionWidth;
            scaleHeight = tileHeight/regionHeight;
        }

    }
}
