package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
/**
 * @Auther WenLong
 * @Date 2025/2/12 15:37
 * @Description 地图管理器
 **/
public class TiledMapManager extends BaseSystem {
    private final static String TAG = TiledMapManager.class.getSimpleName();
    //物理形状图层的名字
    private String phyLayer;
    private String mapName;
    public TiledMap currentMap;
    public Bag<TiledMapTileLayer> phyLayers;

    private AssetSystem assetSystem;
    public TiledMapManager(String mapName,String phyLayer){
        this.mapName = mapName;
        this.phyLayer = phyLayer;
    }

    @Override
    protected void initialize() {
        phyLayers = new Bag<>();
        currentMap = assetSystem.tiledData.get(mapName);
        if (currentMap == null){
            Gdx.app.debug(TAG,"Unable to tiledMap map, please confirm tiledMap name:"+mapName);
            return;
        }
        if (phyLayer.isEmpty()){
            Gdx.app.log(TAG,"To specify the physical layer!phyLayerNames is null!");
            return;
        }
        //需要获取物理形状的图层对象
        MapLayer mapLayer = currentMap.getLayers().get(phyLayer);
        if (mapLayer == null) {
            return;
        }
        phyLayers.add((TiledMapTileLayer) mapLayer);
    }

    @Override
    protected void processSystem() {

    }
}
