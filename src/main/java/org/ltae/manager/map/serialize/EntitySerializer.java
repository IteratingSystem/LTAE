package org.ltae.manager.map.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.*;
import org.ltae.LtaePluginRule;
import org.ltae.component.SerializeComponent;
import org.ltae.manager.JsonManager;
import org.ltae.manager.map.serialize.json.CompData;
import org.ltae.manager.map.serialize.json.EntityDataList;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.manager.map.serialize.json.CompProp;
import org.ltae.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/10 11:02
 * @Description
 **/
public class EntitySerializer {
    public static EntityDataList getEntityDataList(String mapName, MapObjects mapObjects){
        EntityDataList entityDateList = new EntityDataList();
        entityDateList.entities = new ArrayList<>();
        for (MapObject mapObject : mapObjects) {
            String entityName = mapObject.getName();
            MapProperties properties = mapObject.getProperties();

            EntityData entityData = new EntityData();
            entityData.components = new ArrayList<>();
            entityData.mapObjectId = mapObject.getProperties().get("id",0,Integer.class);
            entityData.fromMap = mapName;

            entityData.name = entityName;
            entityData.type = properties.get("type", "", String.class);

            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> compClass : compClasses) {
                String simpleName = compClass.getSimpleName();
                MapProperties property = properties.get(simpleName, null, MapProperties.class);
                if (property == null) {
                    continue;
                }
                CompData compData = new CompData();
                compData.props = new ArrayList<>();
                compData.name = simpleName;

                Field[] fields = compClass.getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (!field.isAnnotationPresent(SerializeParam.class)) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    CompProp compProp = new CompProp();
                    compProp.key = field.getName();
                    compProp.type = type.getName();
                    compProp.value = property.get(field.getName(), null, type);
                    compData.props.add(compProp);
                }
                entityData.components.add(compData);
            }
            //添加默认组件
            for (Class autoCompClass : LtaePluginRule.AUTO_COMP_CLASSES) {
                String simpleName = autoCompClass.getSimpleName();
                if (entityData.hasComp(simpleName)) {
                    continue;
                }
                CompData compData = new CompData();
                compData.props = new ArrayList<>();
                compData.name = simpleName;
                entityData.components.add(compData);
            }
            entityDateList.entities.add(entityData);
        }
        return entityDateList;
    }

    public static EntityDataList getEntityDataList(World world){
        EntityDataList entityDateList = new EntityDataList();
        entityDateList.entities = new ArrayList<>();

        AspectSubscriptionManager aspectSubscriptionManager = world.getSystem(AspectSubscriptionManager.class);
        EntitySubscription allEntities = aspectSubscriptionManager.get(Aspect.all());
        IntBag entities = allEntities.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            EntityData entityData = getEntityData(world,entityId);
            if (entityData == null){
                continue;
            }
            entityDateList.entities.add(entityData);
        }
        return entityDateList;
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

        boolean isSetMapMsg = false;
        for (Component component : components) {
            if (isSetMapMsg){
                break;
            }
            if (component instanceof SerializeComponent serializeComponent) {
                entity.mapObjectId = serializeComponent.mapObject.getProperties().get("id",-1,Integer.class);
                entity.fromMap = serializeComponent.fromMap;
                isSetMapMsg = true;
            }
        }

        entity.components = new ArrayList<>();
        for (Component component : allComps) {
            Class<? extends Component> compClass = component.getClass();
            String compName = compClass.getSimpleName();
            Field[] fields = compClass.getFields();

            CompData compData = new CompData();
            compData.name = compName;
            compData.props = new ArrayList<>();
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
                CompProp prop = new CompProp();
                prop.key = key;
                prop.value = value;
                prop.type = type.getName();

                compData.props.add(prop);
            }
            entity.components.add(compData);
        }
        return entity;
    }
    public static void overlayEntityData(EntityDataList entityDateList, EntityData entityData){
        List<EntityData> entities = entityDateList.entities;
        if (entityDateList.hasEntityData(entityData)) {
            for (EntityData entity : entities) {
                if (entity.equals(entityData)) {
                    entities.remove(entity);
                    entities.add(entityData);
                    break;
                }
            }
            return;
        }
        entities.add(entityData);
    }
    public static void createEntities(World world, EntityDataList entityDataList){
        TagManager tagManager = world.getSystem(TagManager.class);
        if (entityDataList == null) {
            return;
        }
        for (EntityData entityData : entityDataList.entities) {
            //创建
            int entityId = world.create();
            entityData.entityId = entityId;
            //注册tag
            if (!"".equals(entityData.name)) {
                tagManager.register(entityData.name,entityId);
            }
            //注册组件
            List<CompData> components = entityData.components;
            Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> aClass : classes) {
                String simpleName = aClass.getSimpleName();
                for (CompData compData : components) {
                    if (!simpleName.equals(compData.name)) {
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
                    List<CompProp> props = compData.props;
                    for (CompProp prop : props) {
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
    public static String toJson(EntityDataList entityDataList){
        return JsonManager.toJson(entityDataList);
    }
    public static EntityDataList toEntityBag(String entityDataList){
        return JsonManager.fromJson(EntityDataList.class,entityDataList);
    }
}
