package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.LtaePluginRule;
import org.ltae.manager.map.MapManager;

/**
 * @Auther WenLong
 * @Date 2025/7/10 11:38
 * @Description 管理当前地图
 **/
public class TiledMapSystem extends BaseSystem {
    private String current;
    public TiledMapSystem(String mapName){
        current = mapName;
        ObjectMap<String, String> entityLayerNames = new ObjectMap<>();
        ObjectMap<String, String> phyLayerNames = new ObjectMap<>();
        entityLayerNames.put(LtaePluginRule.MAP_NAME,LtaePluginRule.ENTITY_LAYER);
        phyLayerNames.put(LtaePluginRule.MAP_NAME,LtaePluginRule.PHY_LAYER);
        MapManager.init(entityLayerNames,phyLayerNames);
    }
    public TiledMap getTiledMap(){
        MapManager mapManager = MapManager.getInstance();
        return mapManager.getTiledMap(current);
    }
    public MapLayer getPhyLayer(){
        MapManager mapManager = MapManager.getInstance();
        return mapManager.getPhyLayer(current);
    }
    public String getCurrent(){
        return current;
    }
    public void changeCurrent(String current){
        this.current = current;
    }
    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void processSystem() {

    }
}
