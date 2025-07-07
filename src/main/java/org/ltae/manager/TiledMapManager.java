package org.ltae.manager;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @Auther WenLong
 * @Date 2025/7/4 16:09
 * @Description 瓦片地图管理器
 **/
public class TiledMapManager {
    private static final String TAG = TiledMapManager.class.getSimpleName();
    private static final String EXT = ".tmx";
    private static final String THIS_COMP_PKG = "org.ltae.component";
    private static ObjectMap<String, TiledMap> allTileMaps;
    private static Bag<TiledMapTileSet> allTileSets;;


    public static ObjectMap<String, TiledMap> getAllTileMap(){
        if (allTileMaps == null) {
            //瓦片地图数据
            allTileMaps = AssetManager.getInstance().getObjects(EXT,TiledMap.class);
            if (allTileMaps.isEmpty()) {
                Gdx.app.error(TAG,"Failed to getTiledMap;allTileMap is empty,Please load the resources first!");
            }
        }
        return allTileMaps;
    }
    public static TiledMap getTiledMap(String name){
        ObjectMap<String, TiledMap> allTileMap_ = getAllTileMap();
        if (!allTileMap_.containsKey(name)){
            Gdx.app.error(TAG,"Failed to getTiledMap,This tiledMap'name is not in allTiledMap:"+name);
            return null;
        }
        return allTileMap_.get(name);
    }
    public static Bag<TiledMapTileSet> getAllTileSets(){
        if (allTileSets == null){
            allTileSets = new Bag<>();
            ObjectMap.Values<TiledMap> values= getAllTileMap().values();
            while (values.hasNext) {
                TiledMap next = values.next();
                TiledMapTileSets tileSets = next.getTileSets();
                for (TiledMapTileSet tileSet : tileSets) {
                    if (allTileSets.contains(tileSet)) {
                        continue;
                    }
                    allTileSets.add(tileSet);
                }
            }
        }

        return allTileSets;
    }
    public static TiledMapTileSet getTileSet(TiledMapTile tile){

        for (TiledMapTileSet tileSet : getAllTileSets()) {
            if (tileSet.getTile(tile.getId()) == tile) {
                return tileSet;
            }
        }
        return null;
    }
    public static TiledMap getPrefabricatedMap(String perfMapName){
        return getTiledMap(perfMapName);
    }

}
