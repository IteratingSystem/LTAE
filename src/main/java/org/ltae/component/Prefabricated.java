package org.ltae.component;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.system.AssetSystem;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/5/12 11:15
 * @Description 预制件实体,其主要用处是挂载了此组件的实体不会自动创建,提供手动创建的方法,比如子弹之类的实体
 **/
public class Prefabricated extends Component implements TileCompLoader {
    private final static String TAG = Prefabricated.class.getSimpleName();
    private TileDetails tileDetails;
    @Override
    public void loader(TileDetails tileDetails) {
        this.tileDetails = tileDetails;
    }

//    private Entity createEntity(){
//        MapObject mapObject = tileDetails.mapObject;
//        World world = tileDetails.world;
//        Reflections reflections = new Reflections(builder.compPackage);
//        Reflections thisReflections = new Reflections(THIS_COMP_PACKAGE);
//        Set<Class<? extends Component>> compClasses = reflections.getSubTypesOf(Component.class);
//        Set<Class<? extends Component>> thisCompClasses = thisReflections.getSubTypesOf(Component.class);
//        compClasses.addAll(thisCompClasses);
//        //创建实体
//        int entityId = world.create();
//        tileDetails.entity = world.getEntity(entityId);
//        tileDetails.entityId = entityId;
//
//        MapProperties compProps = mapObject.getProperties();
//        //注册TAG
//        if (mapObject.getName() != null) {
//            world.getSystem(TagManager.class).register(mapObject.getName(),tileDetails.entity);
//        }
//        //就算没有维护到tiled中也要初始化的组件
//        for (Class<? extends Component> autoCompClass : builder.autoInitCompClasses) {
//            String simpleName = autoCompClass.getSimpleName();
//            if (compProps.containsKey(simpleName)) {
//                continue;
//            }
//            compProps.put(simpleName,"");
//        }
//
//
//        //遍历组件包内的所有组件,及初始化
//        for (Class<? extends Component> compClass : compClasses) {
//            //跳过其预制件组件
//            if (compClass == Prefabricated.class) {
//                continue;
//            }
//            //组件包列表中列表中没有此组件
//            if (!compProps.containsKey(compClass.getSimpleName())) {
//                continue;
//            }
//
//            ComponentMapper<? extends Component> mapper = world.getMapper(compClass);
//            if (mapper == null) {
//                continue;
//            }
//            Component comp = mapper.create(entityId);
//
//
//            //利用反射给配置了@TiledParam的属性赋值
//            Field[] fields = compClass.getDeclaredFields();
//            for (Field field : fields) {
//                if (!field.isAnnotationPresent(TileParam.class)) {
//                    continue;
//                }
//
//                String fieldName = field.getName();
//                //组件中的参数
//                Object porpObject = compProps.get(compClass.getSimpleName());
//                if (!(porpObject instanceof MapProperties compParams)) {
//                    continue;
//                }
//                //判断是否必填,非必填同时没有这个数据则相安无事
//                if (!compParams.containsKey(fieldName)) {
//                    // 获取 @CompParam 注解实例
//                    TileParam tileParam = field.getAnnotation(TileParam.class);
//                    // 获取 nullable 属性的值
//                    boolean nullable = tileParam.nullable();
//                    if (nullable) {
//                        continue;
//                    }
//                    Gdx.app.error(TAG, "Missing key for field: " + fieldName);
//                    continue;
//                }
//
//                try {
//                    // 获取字段的值
//                    Object value = compParams.get(fieldName);
//                    // 为字段设置值
//                    field.set(comp, value);
//                } catch (IllegalAccessException e) {
//                    Gdx.app.error(TAG, "IllegalAccessException!", e);
//                }
//            }
//
//            if (comp instanceof TileCompLoader tileCompLoader) {
//                tileCompLoader.loader(tileDetails);
//            }
//        }
//        return tileDetails.entity;
//    }
}
