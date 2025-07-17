package org.ltae.manager.map;

import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.AssetManager;
import org.ltae.manager.map.serialize.EntitySerializer;
import org.ltae.manager.map.serialize.json.EntitiesBag;

/**
 * @Auther WenLong
 * @Date 2025/7/9 16:36
 * @Description 地图管理器
 **/
public class MapManager {
    private static String TAG = MapManager.class.getSimpleName();
    private static String EXT = ".tmx";
    private static MapManager instance;
    private ObjectMap<String, TiledMap> allMaps;
    private ObjectMap<String, String> entityLayerNames;
    private ObjectMap<String, String> phyLayerNames;
    private ObjectMap<String, MapLayer> entityLayers;
    private ObjectMap<String, MapObjects> allMapObjects;
    private ObjectMap<String, EntitiesBag> mapEntities;
    private Bag<TiledMapTileSet> tileSets;


    private MapManager(ObjectMap<String, String> entityLayerNames,ObjectMap<String, String> phyLayerNames){
        this.entityLayerNames = entityLayerNames;
        this.phyLayerNames = phyLayerNames;
        allMaps = AssetManager.getInstance().getObjects(EXT,TiledMap.class);
        allMapObjects = new ObjectMap<>();
        mapEntities =new ObjectMap<>();
        tileSets = new Bag<>();
        Array<String> mapKeys = allMaps.keys().toArray();
        for (String mapName : mapKeys) {
            String entityLayerName = entityLayerNames.get(mapName);
            TiledMap tiledMap = getTiledMap(mapName);
            MapLayer entityLayer = tiledMap.getLayers().get(entityLayerName);
            MapObjects mapObjects = entityLayer.getObjects();
            allMapObjects.put(mapName,mapObjects);
            mapEntities.put(mapName,EntitySerializer.getEntities(mapName,mapObjects));
            for (TiledMapTileSet tileSet : tiledMap.getTileSets()) {
                if (tileSets.contains(tileSet)) {
                    continue;
                }
                tileSets.add(tileSet);
            }
        }
    }
    public void saveEntities(String mapName){
        MapObjects mapObjects = getMapObjects(mapName);
        EntitiesBag entitiesJson = EntitySerializer.getEntities(mapName,mapObjects);
        mapEntities.put(mapName,entitiesJson);
    }
    public EntitiesBag getEntities(String mapName){
        return mapEntities.get(mapName);
    }
    public static synchronized void init(ObjectMap<String, String> entityLayerNames,ObjectMap<String, String> phyLayerNames){
        if (instance != null){
            return;
        }
        instance = new MapManager(entityLayerNames,phyLayerNames);
    }
    public static MapManager getInstance(){
        if (instance == null) {
            throw new IllegalStateException(TAG+" is not initialized.Please run "+TAG+".init()!");
        }
        return instance;
    }
    public TiledMap getTiledMap(String mapName){
        if (!allMaps.containsKey(mapName)){
            Gdx.app.error(TAG,"Failed to getTiledMap,allMaps is not contains is map,name:"+mapName);
            return null;
        }
        TiledMap tiledMap = allMaps.get(mapName);
        return tiledMap;
    }
    public MapObjects getMapObjects(String mapName){
        if (!allMapObjects.containsKey(mapName)) {
            Gdx.app.error(TAG,"Failed to getMapObjects,allMapObjects is not contains mapName:"+mapName);
            return null;
        }
        return allMapObjects.get(mapName);
    }
    public MapObject getMapObject(String mapName,int objectId){
        MapObjects mapObjects = getMapObjects(mapName);
        for (MapObject mapObject : mapObjects) {
            Integer id = mapObject.getProperties().get("id", -1, Integer.class);
            if (objectId == id){
                return mapObject;
            }
        }
        Gdx.app.error(TAG,"Failed to getMapObject,No objects with the same id,objectId:"+objectId);
        return null;
    }
    public String getPhyLayerName(String mapName){
        if (!phyLayerNames.containsKey(mapName)){
            Gdx.app.error(TAG,"Failed to getPhyLayer,phyLayerNames is not contains mapName:"+mapName);
        }
        return phyLayerNames.get(mapName);
    }
    public MapLayer getPhyLayer(String mapName){
        String phyLayerName = getPhyLayerName(mapName);
        TiledMap tiledMap = getTiledMap(mapName);
        return tiledMap.getLayers().get(phyLayerName);
    }

    public TiledMapTileSet getTileSet(TiledMapTile tiledMapTile) {
        for (TiledMapTileSet tileSet : tileSets) {
            if (tileSet.getTile(tiledMapTile.getId()) == tiledMapTile) {
                return tileSet;
            }
        }
        return null;
    }
}
