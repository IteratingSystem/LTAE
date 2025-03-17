package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
/**
 * @Auther WenLong
 * @Date 2025/2/12 15:37
 * @Description 地图管理器
 **/
public class TiledMapManager extends BaseSystem {
    private final static String MAP_NAME = "develop";
    //物理形状图层的名字
    private final static String[] PHY_LAYER_NAME = {"BRICK"};
    public TiledMap curMap;
    public Bag<TiledMapTileLayer> phyLayers;

    private AssetSystem assetSystem;

    @Override
    protected void initialize() {
        curMap = assetSystem.tiledData.get(MAP_NAME);

        //需要获取物理形状的图层对象
        phyLayers = new Bag<>();
        for (String layerName : PHY_LAYER_NAME) {
            MapLayer mapLayer = curMap.getLayers().get(layerName);
            if (mapLayer == null) {
                continue;
            }
            phyLayers.add((TiledMapTileLayer) mapLayer);
        }
    }

    @Override
    protected void processSystem() {

    }
}
