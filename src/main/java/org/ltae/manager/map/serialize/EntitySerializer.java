package org.ltae.manager.map.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.utils.Json;
import org.ltae.component.SerializeComponent;
import org.ltae.manager.JsonManager;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.ComponentJson;
import org.ltae.manager.map.serialize.json.EntitiesJson;
import org.ltae.manager.map.serialize.json.EntityJson;
import org.ltae.manager.map.serialize.json.PropertyJson;
import org.ltae.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/10 11:02
 * @Description
 **/
public class EntitySerializer {
    private ComponentConfig componentConfig;

    public EntitySerializer(ComponentConfig componentConfig) {
        this.componentConfig = componentConfig;
    }

    public EntitiesJson getEntitiesJson(String mapName){
        MapManager mapManager = MapManager.getInstance();
        MapObjects mapObjects = mapManager.getMapObjects(mapName);

        EntitiesJson entitiesJson = new EntitiesJson();
        entitiesJson.entities = new Bag<>();
        for (MapObject mapObject : mapObjects) {
            String entityName = mapObject.getName();
            MapProperties properties = mapObject.getProperties();

            EntityJson entityJson = new EntityJson();
            entityJson.components = new Bag<>();
            entityJson.mapObjectId = mapObject.getProperties().get("id",0,Integer.class);

            entityJson.name = entityName;
            entityJson.type = properties.get("type", "", String.class);

            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(componentConfig.compPackages, Component.class);
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
                    propertyJson.type = type.getName();
                    propertyJson.value = property.get(field.getName(), null, type);
                    componentJson.props.add(propertyJson);
                }
                entityJson.components.add(componentJson);
            }
            //添加默认组件
            for (Class<? extends Component> autoCompClass : componentConfig.autoCompClasses) {
                String simpleName = autoCompClass.getSimpleName();
                if (entityJson.hasComp(simpleName)) {
                    continue;
                }
                ComponentJson componentJson = new ComponentJson();
                componentJson.props = new Bag<>();
                componentJson.name = simpleName;
                entityJson.components.add(componentJson);
            }
            entitiesJson.entities.add(entityJson);
        }
        return entitiesJson;
    }

    public EntitiesJson getEntitiesJson(World world){
        EntitiesJson entitiesJson = new EntitiesJson();
        entitiesJson.entities = new Bag<>();

        AspectSubscriptionManager aspectSubscriptionManager = world.getSystem(AspectSubscriptionManager.class);
        EntitySubscription allEntities = aspectSubscriptionManager.get(Aspect.all());

        for (int i = 0; i < allEntities.getEntities().size(); i++) {
            int entityId = allEntities.getEntities().get(i);
            Bag<Component> allComps = new Bag<>();
            world.getEntity(entityId).getComponents(allComps);
            if (allComps.isEmpty()) {
                continue;
            }


            EntityJson entity = new EntityJson();
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

                ComponentJson componentJson = new ComponentJson();
                componentJson.name = compName;
                componentJson.props = new Bag<>();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(SerializeParam.class)) {
                        continue;
                    }
                    String key = field.getName();
                    Class type = field.getType();
                    Object value = new Object();
                    try {
                        value = field.get(component);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    PropertyJson prop = new PropertyJson();
                    prop.key = key;
                    prop.value = value;
                    prop.type = type.getName();

                    componentJson.props.add(prop);
                }
                entity.components.add(componentJson);
            }
            entitiesJson.entities.add(entity);
        }
        return entitiesJson;
    }
    public void createEntities(World world, EntitiesJson entitiesJson){
        TagManager tagManager = world.getSystem(TagManager.class);

        for (EntityJson entityJson : entitiesJson.entities) {
            //创建
            int entityId = world.create();
            entityJson.entityId = entityId;
            //注册tag
            if (!"".equals(entityJson.name)) {
                tagManager.register(entityJson.name,entityId);
            }
            //注册组件
            Bag<ComponentJson> components = entityJson.components;
            Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(componentConfig.compPackages, Component.class);
            for (Class<? extends Component> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                for (ComponentJson componentJson : components) {
                    if (!simpleName.equals(componentJson.name)) {
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
                    Bag<PropertyJson> props = componentJson.props;
                    for (PropertyJson prop : props) {
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
                        serializeComponent.reload(world,entityJson);
                    }
                }
            }
        }
    }
    public String serializerEntitiesJson(EntitiesJson entitiesJson,Json json){
        return json.toJson(entitiesJson);
    }
}
