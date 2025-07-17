package org.ltae.manager.map.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.utils.Json;
import org.ltae.LtaePluginRule;
import org.ltae.component.SerializeComponent;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.ComponentData;
import org.ltae.manager.map.serialize.json.EntitiesBag;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.manager.map.serialize.json.EntityProperty;
import org.ltae.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/10 11:02
 * @Description
 **/
public class EntitySerializer {
    public static EntitiesBag getEntitiesJson(MapObjects mapObjects){
        EntitiesBag entitiesBag = new EntitiesBag();
        entitiesBag.entities = new Bag<>();
        for (MapObject mapObject : mapObjects) {
            String entityName = mapObject.getName();
            MapProperties properties = mapObject.getProperties();

            EntityData entityData = new EntityData();
            entityData.components = new Bag<>();
            entityData.mapObjectId = mapObject.getProperties().get("id",0,Integer.class);

            entityData.name = entityName;
            entityData.type = properties.get("type", "", String.class);

            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> compClass : compClasses) {
                String simpleName = compClass.getSimpleName();
                MapProperties property = properties.get(simpleName, null, MapProperties.class);
                if (property == null) {
                    continue;
                }
                ComponentData componentData = new ComponentData();
                componentData.props = new Bag<>();
                componentData.name = simpleName;

                Field[] fields = compClass.getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (!field.isAnnotationPresent(SerializeParam.class)) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    EntityProperty entityProperty = new EntityProperty();
                    entityProperty.key = field.getName();
                    entityProperty.type = type.getName();
                    entityProperty.value = property.get(field.getName(), null, type);
                    componentData.props.add(entityProperty);
                }
                entityData.components.add(componentData);
            }
            //添加默认组件
            for (Class autoCompClass : LtaePluginRule.AUTO_COMP_CLASSES) {
                String simpleName = autoCompClass.getSimpleName();
                if (entityData.hasComp(simpleName)) {
                    continue;
                }
                ComponentData componentData = new ComponentData();
                componentData.props = new Bag<>();
                componentData.name = simpleName;
                entityData.components.add(componentData);
            }
            entitiesBag.entities.add(entityData);
        }
        return entitiesBag;
    }

    public static EntitiesBag getEntitiesJson(World world){
        EntitiesBag entitiesBag = new EntitiesBag();
        entitiesBag.entities = new Bag<>();

        AspectSubscriptionManager aspectSubscriptionManager = world.getSystem(AspectSubscriptionManager.class);
        EntitySubscription allEntities = aspectSubscriptionManager.get(Aspect.all());
        IntBag entities = allEntities.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            EntityData entityData = getEntityData(world,entityId);
            if (entityData == null){
                continue;
            }
            entitiesBag.entities.add(entityData);
        }
        return entitiesBag;
    }
    public static EntityData getEntityData(World world,int entityId){
        Bag<Component> allComps = new Bag<>();
        world.getEntity(entityId).getComponents(allComps);
        if (allComps.isEmpty()) {
            return null;
        }

        EntityData entity = new EntityData();
        entity.entityId = entityId;
        TagManager tagManager = world.getSystem(TagManager.class);
        entity.name = tagManager.getTag(entityId);
        Bag<Component> components = new Bag<>();
        world.getEntity(entityId).getComponents(components);
        for (Component component : components) {
            if (component instanceof SerializeComponent serializeComponent) {
                entity.mapObjectId = serializeComponent.mapObject.getProperties().get("id",-1,Integer.class);
            }
        }
        entity.components = new Bag<>();
        for (Component component : allComps) {
            Class<? extends Component> compClass = component.getClass();
            String compName = compClass.getSimpleName();
            Field[] fields = compClass.getFields();

            ComponentData componentData = new ComponentData();
            componentData.name = compName;
            componentData.props = new Bag<>();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(SerializeParam.class)) {
                    continue;
                }
                String key = field.getName();
                Class type = field.getType();
                Object value = null;
                try {
                    value = field.get(component);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                EntityProperty prop = new EntityProperty();
                prop.key = key;
                prop.value = value;
                prop.type = type.getName();

                componentData.props.add(prop);
            }
            entity.components.add(componentData);
        }
        return entity;
    }
    public static void overlayEntityData(EntitiesBag entitiesBag,EntityData entityData){
        Bag<EntityData> entities = entitiesBag.entities;
        for (EntityData entity : entities) {
            if (entity.entityId != entityData.entityId) {
                continue;
            }
            entities.remove(entity);
            entities.add(entityData);
        }
    }
    public static void createEntities(World world, EntitiesBag entitiesBag){
        TagManager tagManager = world.getSystem(TagManager.class);

        for (EntityData entityData : entitiesBag.entities) {
            //创建
            int entityId = world.create();
            entityData.entityId = entityId;
            //注册tag
            if (!"".equals(entityData.name)) {
                tagManager.register(entityData.name,entityId);
            }
            //注册组件
            Bag<ComponentData> components = entityData.components;
            Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                for (ComponentData componentData : components) {
                    if (!simpleName.equals(componentData.name)) {
                        continue;
                    }
                    //通过类对象创建组件Mapper
                    ComponentMapper<? extends Component> mapper = world.getMapper(aClass);
                    if (mapper == null) {
                        break;
                    }
                    //通过组件Mapper创建组件
                    Component component = mapper.create(entityId);
                    //写入默认值
                    Bag<EntityProperty> props = componentData.props;
                    for (EntityProperty prop : props) {
                        String key = prop.key;
                        Object value = prop.value;
                        try {
                            Field declaredField = aClass.getDeclaredField(key);
                            if (!declaredField.isAnnotationPresent(SerializeParam.class)) {
                                continue;
                            }
                            declaredField.set(component,value);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //执行reload
                    if (component instanceof SerializeComponent serializeComponent) {
                        serializeComponent.reload(world, entityData);
                        break;
                    }
                }
            }
        }
    }
    public static String serializerEntitiesJson(EntitiesBag entitiesBag, Json json){
        return json.toJson(entitiesBag);
    }
}
