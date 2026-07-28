package org.ltae.system;

import com.artemis.Aspect;
import com.artemis.AspectSubscriptionManager;
import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.component.Pos;
import org.ltae.component.Render;
import org.ltae.component.ZIndex;
import org.ltae.event.EntityEvent;
import org.ltae.serialize.ComponentConfig;
import org.ltae.serialize.EntityBuilder;
import org.ltae.serialize.EntityDeleter;
import org.ltae.serialize.EntitySerializer;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;


/**
 * @Auther WenLong
 * @Date 2025/2/12 16:29
 * @Description
 **/
public class EntityFactory extends BaseSystem {
    private final static String TAG = EntityFactory.class.getSimpleName();
    private TiledMapSystem tiledMapSystem;
    private ObjectMap<String, com.badlogic.gdx.maps.MapObject> prefabricatedObjects;

    public EntityFactory(){
        Bag<Class<? extends Component>> autoCompClasses = new Bag<>();
        autoCompClasses.add(Pos.class);
        autoCompClasses.add(Render.class);
        autoCompClasses.add(ZIndex.class);

        ComponentConfig componentConfig = new ComponentConfig();
        componentConfig.autoCompClasses = autoCompClasses;
    }
    @Override
    protected void initialize() {
    }

    @Override
    protected void processSystem() {

    }
    private void delAndCreateAll(){
        EntityDeleter.deleteAll(world);
        EntityBuilder.buildEntitiesFromSave(world);
    }
    private void delAndCreateAll(EntityData EntityData){
        EntityDeleter.deleteAll(world);
        EntityBuilder.buildEntities(world, EntityData);
    }
    private void buildAll(){
        EntityBuilder.buildEntitiesFromSave(world);
    }
    private void buildEntities(EntityData entityData){
        EntityBuilder.buildEntities(world, entityData);
    }
    private int buildEntity(EntityDatum entityDatum){
        return EntityBuilder.buildEntity(world, entityDatum);
    }
    private EntityData createEntityData(){
        return EntitySerializer.createEntityData(world);
    }
    private EntityDatum createEntityDatum(int entityId){
        return EntitySerializer.createEntityDatum(world,entityId);
    }
    private String serializerEntitiesJson(){
        AspectSubscriptionManager asm = world.getSystem(AspectSubscriptionManager.class);
        IntBag allEntities = asm.get(Aspect.all()).getEntities();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        world.getSystem(WorldSerializationManager.class).save(baos, new SaveFileFormat(allEntities));
        return baos.toString(StandardCharsets.UTF_8);
    }
    private void deleteEntity(int entityId){
        EntityDeleter.deleteEntity(world,entityId);
    }
    private void deleteAll(){
        EntityDeleter.deleteAll(world);
    }
    private void filterDeleteAll(String[] filterEntity){
        EntityDeleter.deleteAll(world,filterEntity);
    }
    @Subscribe
    public void onEvent(EntityEvent event){
        if (event.type == EntityEvent.BUILD_ALL){
            buildAll();
            return;
        }
        if (event.type == EntityEvent.BUILD_ENTITIES){
            buildEntities(event.entityData);
            return;
        }
        if (event.type == EntityEvent.DEL_AND_CREATE_ALL){
            if (event.entityData == null){
                delAndCreateAll();
                return;
            }
            delAndCreateAll(event.entityData);
            return;
        }
        if (event.type == EntityEvent.SERIALIZER_ENTITIES){
            event.serializerEntitiesStr = serializerEntitiesJson();
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
        if (event.type == EntityEvent.FILTER_DEL_ALL){
            filterDeleteAll(event.entityTags);
            return;
        }
        if (event.type == EntityEvent.BUILD_ENTITY){
            event.entityId = buildEntity(event.entityDatum);
            event.entity = world.getEntity(event.entityId);
            return;
        }
        if (event.type == EntityEvent.CREATE_ENTITY_DATUM){
            event.entityDatum = createEntityDatum(event.entityId);
            return;
        }
    }

}
