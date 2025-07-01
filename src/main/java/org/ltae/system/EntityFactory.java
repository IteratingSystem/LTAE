package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.event.EntityEvent;
import org.ltae.tiled.EntityBuilder;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.TiledMapUtils;

/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {
    private final static String TAG = EntityFactory.class.getSimpleName();
    private TiledMapManager tiledMapManager;

    private ObjectMap<String, MapObject> prefabricatedObjects;
    public SystemDetails systemDetails;
    private EntityBuilder entityBuilder;



    public EntityFactory(String componentPkg, String statePkg, String b2dListenerPkg, String entityLayer, float worldScale){
        Bag<Class<? extends Component>> autoCompClasses = new Bag<>();
        autoCompClasses.add(Pos.class);
        autoCompClasses.add(Render.class);
        autoCompClasses.add(ZIndex.class);

        systemDetails = new SystemDetails();
        systemDetails.worldScale = worldScale;
        systemDetails.entityLayer = entityLayer;
        systemDetails.statePkg = statePkg;
        systemDetails.b2dListenerPkg = b2dListenerPkg;
        systemDetails.componentPkg = componentPkg;
        systemDetails.autoCompClasses = autoCompClasses;

    }
    @Override
    protected void initialize() {
        systemDetails.world = world;
        systemDetails.tiledMap = tiledMapManager.currentMap;
        entityBuilder = new EntityBuilder(systemDetails);
        entityBuilder.createAllEntity();
    }

    @Override
    protected void processSystem() {

    }

    private void initPrefabObjects(){
        prefabricatedObjects = new ObjectMap<>();
        TiledMap prefabricatedMap = tiledMapManager.getPrefabricatedMap();
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
    private MapObject getPrefabricatedObject(String name){
        if (prefabricatedObjects == null) {
            initPrefabObjects();
        }
        if (!prefabricatedObjects.containsKey(name)) {
            Gdx.app.error(TAG,"Failed to getPrefabricatedObject,name is not in prefabricatedObjects: "+name);
            return null;
        }
        return prefabricatedObjects.get(name);
    }
    private Entity createEntity(MapObject mapObject,float x,float y){
        Entity entity = entityBuilder.createEntity(mapObject);
        Pos pos = entity.getComponent(Pos.class);
        pos.x = x;
        pos.y = y;
        return entity;
    }
    private Entity createPrefabEntity(String name){
        MapObject prefabricatedObject = getPrefabricatedObject(name);
        return entityBuilder.createEntity(prefabricatedObject);
    }

    private Entity createPrefabEntity(String name,float x,float y){
        MapObject prefabricatedObject = getPrefabricatedObject(name);
        return createEntity(prefabricatedObject,x,y);
    }

    @Subscribe
    public void onEvent(EntityEvent event){
        if (event.type == EntityEvent.CREATE_ENTITY){
            event.entity = createEntity(event.mapObject,event.x,event.y);
            return ;
        }
        if (event.type == EntityEvent.CREATE_PREFAB){
            if (event.x == 0 && event.y == 0) {
                event.entity = createPrefabEntity(event.name);
                return;
            }
            event.entity = createPrefabEntity(event.name,event.x,event.y);
            return;
        }
        if (event.type == EntityEvent.GET_MAP_OBJECT){
            event.mapObject = getPrefabricatedObject(event.name);
            return;
        }
    }
}
