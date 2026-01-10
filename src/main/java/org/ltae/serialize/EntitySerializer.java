package org.ltae.serialize;

import com.artemis.*;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.utils.Array;
import org.ltae.LtaePluginRule;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.manager.JsonManager;
import org.ltae.serialize.data.*;
import org.ltae.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/7/10 11:02
 * @Description
 **/
public class EntitySerializer {
    public static EntityData createEntityData(String mapName, MapObjects mapObjects){
        EntityData entityDateList = new EntityData();
        for (MapObject mapObject : mapObjects) {
            String entityName = mapObject.getName();
            MapProperties properties = mapObject.getProperties();

            EntityDatum entityDatum = new EntityDatum();
            entityDatum.compMirrors = new Array<>();
            entityDatum.mapObjectId = mapObject.getProperties().get("id",0,Integer.class);
            entityDatum.fromMap = mapName;

            entityDatum.name = entityName;
            entityDatum.type = properties.get("type", "", String.class);

            Set<Class<? extends Component>> compClasses = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
            for (Class<? extends Component> compClass : compClasses) {
                String simpleName = compClass.getSimpleName();
                MapProperties property = properties.get(simpleName, null, MapProperties.class);
                if (property == null) {
                    continue;
                }
                CompMirror compMirror = new CompMirror();
                compMirror.properties = new Properties();
                compMirror.simpleName = simpleName;

                Field[] fields = compClass.getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (!field.isAnnotationPresent(SerializeParam.class)) {
                        continue;
                    }
                    Class<?> type = field.getType();
                    Property compProp = new Property();
                    compProp.key = field.getName();
                    compProp.type = type.getName();
                    compProp.value = property.get(field.getName(), null, type);
                    compMirror.properties.add(compProp);
                }
                entityDatum.compMirrors.add(compMirror);
            }
            //添加默认组件
            for (Class autoCompClass : LtaePluginRule.AUTO_COMP_CLASSES) {
                String simpleName = autoCompClass.getSimpleName();
                if (entityDatum.hasComp(simpleName)) {
                    continue;
                }
                CompMirror compMirror = new CompMirror();
                compMirror.properties = new Properties();
                compMirror.simpleName = simpleName;
                entityDatum.compMirrors.add(compMirror);
            }
            entityDateList.add(entityDatum);
        }
        entityDateList.setDataFrom(EntityData.FROM_MAP);
        return entityDateList;
    }

    public static EntityData createEntityData(World world){
        EntityData entityDateList = new EntityData();

        AspectSubscriptionManager aspectSubscriptionManager = world.getSystem(AspectSubscriptionManager.class);
        EntitySubscription allEntities = aspectSubscriptionManager.get(Aspect.all());
        IntBag entities = allEntities.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            EntityDatum entityDatum = createEntityDatum(world,entityId);
            if (entityDatum == null){
                continue;
            }
            entityDateList.add(entityDatum);
        }
        entityDateList.setDataFrom(EntityData.FROM_WORLD);
        return entityDateList;
    }
    public static EntityDatum createEntityDatum(World world, int entityId){
        Bag<Component> allComps = new Bag<>();
        world.getEntity(entityId).getComponents(allComps);
        if (allComps.isEmpty()) {
            return null;
        }

        EntityDatum entity = new EntityDatum();
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

        entity.compMirrors = new Array<>();
        for (Component component : allComps) {
            if (component instanceof SerializeComponent serializeComponent) {
                serializeComponent.beforeSerialization();
            }


            Class<? extends Component> compClass = component.getClass();
            String compName = compClass.getSimpleName();
            Field[] fields = compClass.getFields();

            CompMirror compMirror = new CompMirror();
            compMirror.simpleName = compName;
            compMirror.properties = new Properties();
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
                Property prop = new Property();
                prop.key = key;
                prop.value = value;
                prop.type = type.getName();

                compMirror.properties.add(prop);
            }
            entity.compMirrors.add(compMirror);
        }
        entity.dataFrom = EntityData.FROM_WORLD;
        return entity;
    }
    //覆盖entityDatum,更新了新的数据覆盖上去,相对于复制过去替换掉
    public static void overlayEntityData(EntityData entityData, EntityDatum entityDatum){
        if (entityData.hasEntityData(entityDatum)) {
            for (EntityDatum entity : entityData) {
                if (entity.equals(entityDatum)) {
                    entityData.removeValue(entity,true);
                    entityData.add(entityDatum);
                    break;
                }
            }
            return;
        }
        entityData.add(entityDatum);
    }
    public static int buildEntity(World world,EntityDatum entityDatum){
        TagManager tagManager = world.getSystem(TagManager.class);
        //创建
        int entityId = world.create();
        entityDatum.entityId = entityId;
        //注册tag
        if (!"".equals(entityDatum.name)) {
            tagManager.register(entityDatum.name,entityId);
        }
        //注册组件
        Array<CompMirror> components = entityDatum.compMirrors;
        Set<Class<? extends Component>> classes = ReflectionUtils.getClasses(new String[]{LtaePluginRule.COMPONENT_PKG,LtaePluginRule.LTAE_COMPONENT_PKG}, Component.class);
        for (Class<? extends Component> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            for (CompMirror compMirror : components) {
                if (!simpleName.equals(compMirror.simpleName)) {
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
                Array<Property> props = compMirror.properties;
                for (Property prop : props) {
                    String key = prop.key;
                    Object value = prop.value;
                    try {
                        Field declaredField = aClass.getDeclaredField(key);
                        if (!declaredField.isAnnotationPresent(SerializeParam.class)) {
                            continue;
                        }
                        if(value != null){
                            declaredField.set(component,value);
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                //执行reload
                if (component instanceof SerializeComponent serializeComponent) {
                    serializeComponent.reload(world, entityDatum);
                    break;
                }
            }
        }
        return entityId;
    }
    public static void buildEntities(World world, EntityData entityData){
        if (entityData == null) {
            return;
        }
        for (EntityDatum entityDatum : entityData) {
            buildEntity(world,entityDatum);
        }
    }
    public static String toJson(EntityData entityData){
        return JsonManager.toJson(entityData);
    }
    public static EntityData toEntityBag(String entityDataList){
        return JsonManager.fromJson(EntityData.class,entityDataList);
    }
}
