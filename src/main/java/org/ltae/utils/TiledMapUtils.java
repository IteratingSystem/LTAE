package org.ltae.utils;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * @Auther WenLong
 * @Date 2025/5/14 10:24
 * @Description 地图工具
 **/
public class TiledMapUtils {
    private final static String TAG = TiledMapUtils.class.getSimpleName();
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
}
