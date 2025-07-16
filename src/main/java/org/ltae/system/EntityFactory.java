package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.LtaePluginRule;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.event.EntityEvent;
import org.ltae.manager.map.serialize.EntityDeleter;
import org.ltae.manager.JsonManager;
import org.ltae.manager.map.serialize.ComponentConfig;
import org.ltae.manager.map.serialize.EntityBuilder;
import org.ltae.manager.map.serialize.EntitySerializer;
import org.ltae.manager.map.serialize.json.EntitiesBag;


/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {
    private final static String TAG = EntityFactory.class.getSimpleName();
    private TiledMapSystem tiledMapSystem;
    private ObjectMap<String, MapObject> prefabricatedObjects;
    private EntitySerializer entitySerializer;
    private EntityBuilder entityBuilder;

    public EntityFactory(){
        Bag<Class<? extends Component>> autoCompClasses = new Bag<>();
        autoCompClasses.add(Pos.class);
        autoCompClasses.add(Render.class);
        autoCompClasses.add(ZIndex.class);

        ComponentConfig componentConfig = new ComponentConfig();
        componentConfig.compPackages = new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG};
        componentConfig.autoCompClasses = autoCompClasses;

        entitySerializer = new EntitySerializer(componentConfig);
        entityBuilder = new EntityBuilder(entitySerializer);
    }
    @Override
    protected void initialize() {
    }

    @Override
    protected void processSystem() {

    }
    private void createAll(){
        EntityDeleter.deleteAll(world);
        world.process();
        entityBuilder.buildEntities(world,tiledMapSystem.getCurrent());
    }
    private void createAll(EntitiesBag entitiesBag){
        EntityDeleter.deleteAll(world);
        entityBuilder.buildEntities(world, entitiesBag);
    }
    private EntitiesBag getEntitiesJson(){
        return entitySerializer.getEntitiesJson(world);
    }
    private String serializerEntitiesJson(){
        EntitiesBag entitiesBag = entitySerializer.getEntitiesJson(world);
        return JsonManager.toJson(entitiesBag);
    }
    private void deleteEntity(int entityId){
        EntityDeleter.deleteEntity(world,entityId);
    }
    private void deleteAll(){
        EntityDeleter.deleteAll(world);
    }
    @Subscribe
    public void onEvent(EntityEvent event){
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
//        if (event.type == CreateEntityEvent.GET_MAP_OBJECT){
//            event.mapObject = getPrefObject(event.name);
//            return;
//        }
        if (event.type == EntityEvent.CREATE_ALL){
            if (event.entitiesBag == null){
                createAll();
                return;
            }
            createAll(event.entitiesBag);
            return;
        }
//        if (event.type == CreateEntityEvent.ADD_AUTO_COMP){
//            addAutoComp(event.compClass);
//            return;
//        }
        if (event.type == EntityEvent.SERIALIZER_ENTITIES){
            event.entitiesStr = serializerEntitiesJson();
            return;
        }
        if (event.type == EntityEvent.DELETE_ENTITY){
            deleteEntity(event.entityId);
            return;
        }
        if (event.type == EntityEvent.DELETE_ALL){
            deleteAll();
            return;
        }
    }

}
