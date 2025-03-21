package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;

/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class Render extends Component implements TileCompLoader {
    public boolean visible = true;
    public float offsetX = 0;
    public float offsetY = 0;
    public float scaleW = 1;
    public float scaleH = 1;
    public TextureRegion keyFrame;


    @Override
    public void loader(TileDetails tileDetails) {
        MapObject mapObject = tileDetails.mapObject;
        if (mapObject instanceof TextureMapObject textureMapObject) {
            keyFrame = textureMapObject.getTextureRegion();
        }
    }
}
