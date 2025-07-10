package org.ltae.manager.map;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.manager.AssetManager;

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
    private Bag<TiledMapTileSet> tileSets;

    private MapManager(ObjectMap<String, String> entityLayerNames,ObjectMap<String, String> phyLayerNames){
        allMaps = AssetManager.getInstance().getObjects(EXT,TiledMap.class);
        this.entityLayerNames = entityLayerNames;
        this.phyLayerNames = phyLayerNames;
        entityLayers = new ObjectMap<>();
        allMapObjects = new ObjectMap<>();
        tileSets = new Bag<>();
        ObjectMap.Entries<String, String> layerNames = entityLayerNames.iterator();
        while (layerNames.hasNext()) {
            ObjectMap.Entry<String, String> next = layerNames.next();
            String layerName = next.value;
            TiledMap tiledMap = getTiledMap(next.key);
            MapLayer entityLayer = tiledMap.getLayers().get(layerName);
            entityLayers.put(next.key,entityLayer);
            allMapObjects.put(next.key,entityLayer.getObjects());
            for (TiledMapTileSet tileSet : tiledMap.getTileSets()) {
                if (tileSets.contains(tileSet)) {
                    continue;
                }
                tileSets.add(tileSet);
            }
        }
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
        if (allMaps.containsKey(mapName)){
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
