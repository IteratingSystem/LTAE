package org.ltae.manager;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import org.ltae.LtaePluginRule;
import org.ltae.tiled.SerializeParam;
import org.ltae.utils.ReflectionUtils;
import org.ltae.utils.serialize.json.ComponentJson;
import org.ltae.utils.serialize.json.EntitiesJson;
import org.ltae.utils.serialize.json.EntityJson;
import org.ltae.utils.serialize.json.PropertyJson;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/4 16:09
 * @Description 瓦片地图管理器
 **/
public class TiledMapManager {
    private static final String TAG = TiledMapManager.class.getSimpleName();
    private static final String EXT = "tmx";
    private static final String THIS_COMP_PKG = "org.ltae.component";
    private static ObjectMap<String, TiledMap> allTileMap;


    public static ObjectMap<String, TiledMap> getAllTileMap(){
        if (allTileMap == null) {
            //瓦片地图数据
            allTileMap = AssetManager.getInstance().getObjects(EXT,TiledMap.class);
            if (allTileMap.isEmpty()) {
                Gdx.app.error(TAG,"Failed to getTiledMap;allTileMap is empty,Please load the resources first!");
            }
        }
        return allTileMap;
    }
    public static TiledMap getTiledMap(String name){
        ObjectMap<String, TiledMap> allTileMap_ = getAllTileMap();
        if (!allTileMap_.containsKey(name)){
            Gdx.app.error(TAG,"Failed to getTiledMap,This tiledMap'name is not in allTiledMap:"+name);
            return null;
        }
        return allTileMap_.get(name);
    }

    public static EntitiesJson getEntitiesJson(String tiledMapName,String entityLayerName,String componentPkg){
        return getEntitiesJson(getTiledMap(tiledMapName),entityLayerName);
    }
    public static EntitiesJson getEntitiesJson(TiledMap tiledMap,String entityLayerName){
        MapLayers layers = tiledMap.getLayers();
        MapLayer entityLayer = layers.get(entityLayerName);
        MapObjects mapObjects = entityLayer.getObjects();
        EntitiesJson entitiesJson = new EntitiesJson();
        entitiesJson.entities = new Bag<>();
        for (MapObject mapObject : mapObjects) {
            String entityName = mapObject.getName();
            MapProperties properties = mapObject.getProperties();

            EntityJson entityJson = new EntityJson();
            entityJson.components = new Bag<>();
            entityJson.mapObject = mapObject;
            entityJson.name = entityName;
            entityJson.type = properties.get("type", "", String.class);


            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(new String[]{THIS_COMP_PKG, LtaePluginRule.COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> compClass : compClasses) {
                String simpleName = compClass.getSimpleName();
                MapProperties property = properties.get(simpleName, null, MapProperties.class);
                if (property == null) {
                    continue;
                }
                ComponentJson componentJson = new ComponentJson();
                componentJson.props = new Bag<>();
                componentJson.name = simpleName;

                Field[] fields = compClass.getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (!field.isAnnotationPresent(SerializeParam.class)) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    PropertyJson propertyJson = new PropertyJson();
                    propertyJson.key = field.getName();
                    propertyJson.type = type;
                    propertyJson.value = property.get(field.getName(), null, type);
                    componentJson.props.add(propertyJson);
                }
                entityJson.components.add(componentJson);
            }
            entitiesJson.entities.add(entityJson);
        }
        return entitiesJson;
    }
}
