package org.ltae.utils;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.Iterator;

/**
 * @Auther WenLong
 * @Date 2025/5/14 10:24
 * @Description 地图工具
 **/
public class TiledMapUtils {
    private final static String TAG = TiledMapUtils.class.getSimpleName();
    public static void logProperties(MapProperties mapProperties){
        Iterator<String> keys = mapProperties.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = mapProperties.get(key);
            Gdx.app.log(TAG,key+":"+value);
        }
    }
    public static Bag<MapObject> getObjects(TiledMap tiledMap, String layerName){
        MapLayers layers = tiledMap.getLayers();
        MapLayer layer = layers.get(layerName);
        if (layer == null) {
            Gdx.app.error(TAG,"Failed to 'getObjects','layer' is null,This 'layerName' is not in the 'tiledMap'");
            return null;
        }
        Bag<MapObject> mapObjects = new Bag<>();
        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            mapObjects.add(object);
        }
        return mapObjects;
    }
    public static Bag<MapObject> getObjects(TiledMap tiledMap){
        MapLayers layers = tiledMap.getLayers();
        Bag<MapObject> mapObjects = new Bag<>();
        for (MapLayer layer : layers) {
            Bag<MapObject> objects = getObjects(tiledMap, layer.getName());
            mapObjects.addAll(objects);
        }
        return mapObjects;
    }


    //填充图像边缘区域,修复地图分裂问题
    public static void fillSplit(TiledMap tiledMap){
        //解决地图开裂的bug
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer tileLayer) {
                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null) {
                            TextureRegion region = cell.getTile().getTextureRegion();
                            TiledMapUtils.fillSplit(region);
                        }
                    }
                }
            }
        }
    }
    public static void fillSplit(TextureRegion[][] region) {
        for (TextureRegion[] array : region) {
            for (TextureRegion texture : array) {
                fillSplit(texture);
            }
        }
    }
    public static void fillSplit(TextureRegion region) {
        // 填充值
        float fix = 0.01f;
        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight); // Trims                                                                                                                                      // region
    }
}
