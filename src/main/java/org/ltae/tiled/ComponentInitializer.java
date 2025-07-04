package org.ltae.tiled;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:34
 * @Description
 **/
public class ComponentInitializer {
    private final static String TAG = ComponentInitializer.class.getSimpleName();
    private final static String THIS_COMP_PACKAGE = "org.ltae.component";
    private SystemDetails systemDetails;

    private Set<Class<? extends Component>> compClasses;
    public ComponentInitializer(SystemDetails systemDetails){
        this.systemDetails = systemDetails;

        String componentPkg = systemDetails.componentPkg;
        Reflections compPkg = new Reflections(componentPkg);
        Reflections thisCompPkg = new Reflections(THIS_COMP_PACKAGE);
        compClasses = new HashSet<>();
        compClasses.addAll(compPkg.getSubTypesOf(Component.class));
        compClasses.addAll(thisCompPkg.getSubTypesOf(Component.class));
    }


    private void initComponent(EntityDetails entityDetails,Component component) {
        //利用反射给配置了@TiledParam的属性赋值
        Field[] fields = component.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(SerializeParam.class)) {
                continue;
            }

            String fieldName = field.getName();
            //组件中的参数
            MapObject mapObject = entityDetails.mapObject;
            MapProperties compsProps = mapObject.getProperties();
            Object porpObject = compsProps.get(component.getClass().getSimpleName());
            if (!(porpObject instanceof MapProperties compProps)) {
                continue;
            }
            //判断是否必填,非必填同时没有这个数据则相安无事
            if (!compProps.containsKey(fieldName)) {
                // 获取 @CompParam 注解实例
                SerializeParam serializeParam = field.getAnnotation(SerializeParam.class);
                // 获取 nullable 属性的值
                boolean nullable = serializeParam.nullable();
                if (nullable) {
                    continue;
                }
                Gdx.app.error(TAG, "Missing key for field: " + fieldName);
                continue;
            }

            try {
                // 获取字段的值
                Object value = compProps.get(fieldName);
                // 为字段设置值
                field.set(component, value);
            } catch (IllegalAccessException e) {
                Gdx.app.error(TAG, "IllegalAccessException!", e);
            }
        }

        if (component instanceof TiledSerializeLoader tiledSerializeLoader) {
            tiledSerializeLoader.loader(systemDetails,entityDetails);
        }
    }

    private Component createComponent (int entityId,Class<? extends Component> compClass) {
        //通过类对象创建组件Mapper
        World world = systemDetails.world;
        ComponentMapper<? extends Component> mapper = world.getMapper(compClass);
        if (mapper == null) {
            return null;
        }
        //通过组件Mapper创建组件
        return mapper.create(entityId);
    }
    private void createAndInit(EntityDetails entityDetails,Class<? extends Component> compClass){
        //创建及初始化
        int entityId = entityDetails.entityId;
        Component component = createComponent(entityId, compClass);
        initComponent(entityDetails,component);
    }
    public void createAndInitAll(EntityDetails entityDetails){
        //注册TAG
        World world = systemDetails.world;
        MapObject mapObject = entityDetails.mapObject;
        int entityId = entityDetails.entityId;
        TagManager tagManager = world.getSystem(TagManager.class);
        tagManager.register(mapObject.getName(),entityId);

        //就算没有维护到tiled中也要初始化的组件,在此加入到其属性列表
        Bag<Class<? extends Component>> autoCompClasses = systemDetails.autoCompClasses;
        MapProperties compsProps = mapObject.getProperties();
        for (Class<? extends Component> autoCompClass : autoCompClasses) {
            String simpleName = autoCompClass.getSimpleName();
            if (compsProps.containsKey(simpleName)) {
                continue;
            }
            compsProps.put(simpleName,"");
        }

        //遍历所有组件及初始化
        for (Class<? extends Component> compClass : compClasses) {
            //属性列表中没有此组件
            if (!compsProps.containsKey(compClass.getSimpleName())) {
                continue;
            }
            createAndInit(entityDetails,compClass);
        }
    }
}
