package org.ltae.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Json;
import org.ltae.component.SerializeComponent;
import org.ltae.manager.JsonManager;
import org.ltae.utils.ReflectionUtils;
import org.ltae.serialize.json.ComponentJson;
import org.ltae.serialize.json.EntitiesJson;
import org.ltae.serialize.json.EntityJson;
import org.ltae.serialize.json.PropertyJson;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/4 11:25
 * @Description 序列化工具
 **/
public class SerializeUtils {
    private static Json json;
    static{
        json = new Json();
    }

    public static String serializeEntities(World world){
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
                    prop.type = type;

                    componentJson.props.add(prop);
                }
                entity.components.add(componentJson);
            }
            entitiesJson.entities.add(entity);
        }
        Json json = JsonManager.getInstance();
        return json.toJson(entitiesJson);
    }
    public static void createEntities(World world, EntitiesJson entitiesJson,String[] compPackages){
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
            Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(compPackages, Component.class);
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
    public static EntitiesJson getEntitiesJson(TiledMap tiledMap, String entityLayerName,String[] compPackages,Bag<Class<? extends Component>> autoCompClasses){
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


            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(compPackages, Component.class);
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
            //添加默认组件
            for (Class<? extends Component> autoCompClass : autoCompClasses) {
                String simpleName = autoCompClass.getSimpleName();
                if (hasComp(entityJson,simpleName)) {
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
    private static boolean hasComp(EntityJson entityJson,String compName){
        for (ComponentJson component : entityJson.components) {
            if (component.name.equals(compName)) {
                return true;
            }
        }
        return false;
    }
}
