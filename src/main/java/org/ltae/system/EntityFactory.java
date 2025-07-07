package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.LtaePluginRule;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.event.CreateEntityEvent;
import org.ltae.serialize.EntityBuilder;
import org.ltae.utils.TiledMapUtils;


/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {
    private final static String TAG = EntityFactory.class.getSimpleName();
    private ObjectMap<String, MapObject> prefabricatedObjects;
    private Bag<Class<? extends Component>> autoCompClasses;

    public EntityFactory(){
        autoCompClasses = new Bag<>();
        autoCompClasses.add(Pos.class);
        autoCompClasses.add(Render.class);
        autoCompClasses.add(ZIndex.class);
    }
    @Override
    protected void initialize() {
    }

    @Override
    protected void processSystem() {

    }

    private void initPrefabObjects(){
        prefabricatedObjects = new ObjectMap<>();
        TiledMap prefabricatedMap = org.ltae.manager.TiledMapManager.getPrefabricatedMap(LtaePluginRule.PREFABRICATED_MAP_NAME);
        if (prefabricatedMap == null){
            Gdx.app.error(TAG,"Failed to initPrefabricatedObjects,Not find TiledMap");
            return;
        }
        Bag<MapObject> mapObjects = TiledMapUtils.getObjects(prefabricatedMap);
        for (MapObject object : mapObjects) {
            String name = object.getName();
            if (name == null || name.isEmpty()) {
                continue;
            }
            prefabricatedObjects.put(name,object);
        }
    }
    private MapObject getPrefObject(String name){
        if (prefabricatedObjects == null) {
            initPrefabObjects();
        }
        if (!prefabricatedObjects.containsKey(name)) {
            Gdx.app.error(TAG,"Failed to getPrefabricatedObject,name is not in prefabricatedObjects: "+name);
            return null;
        }
        return prefabricatedObjects.get(name);
    }
//    private Entity createEntity(MapObject mapObject,float x,float y){
//        Entity entity = entityBuilder.createEntity(mapObject);
//        Pos pos = entity.getComponent(Pos.class);
//        pos.x = x;
//        pos.y = y;
//        return entity;
//    }
//    private Entity createPrefabEntity(String name){
//        MapObject prefabricatedObject = getPrefObject(name);
//        return entityBuilder.createEntity(prefabricatedObject);
//    }

//    private Entity createPrefabEntity(String name,float x,float y){
//        MapObject prefabricatedObject = getPrefObject(name);
//        return createEntity(prefabricatedObject,x,y);
//    }
    private void createAll(){
        EntityBuilder.createAll(world,LtaePluginRule.MAP_NAME, LtaePluginRule.ENTITY_LAYER, new String[]{LtaePluginRule.LTAE_COMPONENT_PKG,LtaePluginRule.COMPONENT_PKG},autoCompClasses);
    }
    private void addAutoComp(Class<? extends Component> zClass){
        if (autoCompClasses.contains(zClass)) {
            return;
        }
        autoCompClasses.add(zClass);
    }
    @Subscribe
    public void onEvent(CreateEntityEvent event){
//        if (event.type == CreateEntityEvent.CREATE_ENTITY){
//            event.entity = createEntity(event.mapObject,event.x,event.y);
//            return ;
//        }
//        if (event.type == CreateEntityEvent.CREATE_PREFAB){
//            if (event.x == 0 && event.y == 0) {
//                event.entity = createPrefabEntity(event.name);
//                return;
//            }
//            event.entity = createPrefabEntity(event.name,event.x,event.y);
//            return;
//        }
        if (event.type == CreateEntityEvent.GET_MAP_OBJECT){
            event.mapObject = getPrefObject(event.name);
            return;
        }
        if (event.type == CreateEntityEvent.CREATE_ALL){
            createAll();
            return;
        }
        if (event.type == CreateEntityEvent.ADD_AUTO_COMP){
            addAutoComp(event.compClass);
            return;
        }
    }

}
