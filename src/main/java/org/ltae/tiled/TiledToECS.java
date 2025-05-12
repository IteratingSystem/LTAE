package org.ltae.tiled;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import jdk.jfr.Percentage;
import org.ltae.component.Prefabricated;
import org.ltae.system.AssetSystem;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Auther WenLong
 * @Date 2025/2/12 16:27
 * @Description tiled中的对象传入为实体
 **/
public class TiledToECS {
    private final static String TAG = TiledToECS.class.getSimpleName();
    private final static String THIS_COMP_PACKAGE = "org.ltae.component";
    private static Builder builder;
    private static Set<Class<? extends Component>> compClasses;
    private static TileDetails tileDetails;
    private TiledToECS() {}
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建实体
     * createMode == CreateMode.ENTITY 时创建常规实体,如果是预制件,则只创建带有预制件组件的实体
     * createMode == CreateMode.PREFABRICATED 时由预制件创建实体(由预制件描述的实体),则不添加TAG,同时忽略其组件中的预制件组件
     * @param mapObject
     * @param createMode
     */
    public static Entity createEntity(MapObject mapObject, CreateMode createMode){
        //创建实体
        World world = builder.world;
        int entityId = world.create();
        //每一个对象都是这几个参数发生改变
        tileDetails.entity = world.getEntity(entityId);
        tileDetails.entityId = entityId;
        tileDetails.mapObject = mapObject;
        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tileDetails.tiledMapTile = tileMapObject.getTile();
        }

        //判断是否是预制件
        MapProperties compProps = mapObject.getProperties();
        boolean isPrefabricated = compProps.containsKey(Prefabricated.class.getSimpleName());

        //注册TAG
        if (!isPrefabricated
        || (isPrefabricated && createMode == CreateMode.ENTITY)
        && mapObject.getName() != null) {
            world.getSystem(TagManager.class).register(mapObject.getName(),tileDetails.entity);
        }

        //就算没有维护到tiled中也要初始化的组件
        for (Class<? extends Component> autoCompClass : builder.autoInitCompClasses) {
            String simpleName = autoCompClass.getSimpleName();
            if (compProps.containsKey(simpleName)) {
                continue;
            }
            compProps.put(simpleName,"");
        }

        //遍历所有组件及初始化
        for (Class<? extends Component> compClass : compClasses) {
            //拥有预制件,同时为常规创建实体模式,则只需要预制件组件
            if (isPrefabricated && createMode == CreateMode.ENTITY){
                if (compClass != Percentage.class){
                    continue;
                }
            }

            //拥有预制件,同时为常规创建预制件模式,则不需要预制件组件
            if (isPrefabricated && createMode == CreateMode.PREFABRICATED){
                if (compClass == Percentage.class){
                    continue;
                }
            }

            //属性列表中没有此组件
            if (!compProps.containsKey(compClass.getSimpleName())) {
                continue;
            }

            //通过类对象创建组件Mapper
            ComponentMapper<? extends Component> mapper = world.getMapper(compClass);
            if (mapper == null) {
                continue;
            }
            //通过组件Mapper创建组件
            Component component = mapper.create(entityId);
            //利用反射给配置了@TiledParam的属性赋值
            Field[] fields = compClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(TileParam.class)) {
                    continue;
                }

                String fieldName = field.getName();
                //组件中的参数
                Object porpObject = compProps.get(compClass.getSimpleName());
                if (!(porpObject instanceof MapProperties compParams)) {
                    continue;
                }
                //判断是否必填,非必填同时没有这个数据则相安无事
                if (!compParams.containsKey(fieldName)) {
                    // 获取 @CompParam 注解实例
                    TileParam tileParam = field.getAnnotation(TileParam.class);
                    // 获取 nullable 属性的值
                    boolean nullable = tileParam.nullable();
                    if (nullable) {
                        continue;
                    }
                    Gdx.app.error(TAG, "Missing key for field: " + fieldName);
                    continue;
                }

                try {
                    // 获取字段的值
                    Object value = compParams.get(fieldName);
                    // 为字段设置值
                    field.set(component, value);
                } catch (IllegalAccessException e) {
                    Gdx.app.error(TAG, "IllegalAccessException!", e);
                }
            }

            if (component instanceof TileCompLoader tileCompLoader) {
                tileCompLoader.loader(tileDetails);
            }
        }
        return tileDetails.entity;
    }
    public void parse() {
        //组件类对象列表
        Reflections compPkg = new Reflections(builder.compPackage);
        Reflections thisCompPkg = new Reflections(THIS_COMP_PACKAGE);
        compClasses = new HashSet<>();
        compClasses.addAll(compPkg.getSubTypesOf(Component.class));
        compClasses.addAll(thisCompPkg.getSubTypesOf(Component.class));
        //创建用于数据传输的中间对象
        tileDetails = new TileDetails();
        tileDetails.world = builder.world;
        tileDetails.assetSystem = builder.world.getSystem(AssetSystem.class);
        tileDetails.b2dWorld = builder.b2dWorld;
        tileDetails.tiledMap = builder.tiledMap;
        tileDetails.worldScale = builder.worldScale;
        tileDetails.statePackage = builder.statePackage;
        tileDetails.contactListenerPackage = builder.contactListenerPackage;
        tileDetails.compClasses = compClasses;

        for (MapObject mapObject : builder.mapObjects) {
            createEntity(mapObject,CreateMode.ENTITY);
        }
    }


    /**
     * 创建实体时,其创建模式
     */
    public enum CreateMode{
        //创建为常规实体,预制件对象创建出来的实体只拥有预制件组件
        ENTITY,
        //从预制件实体创建子实体,忽略预制件组件
        PREFABRICATED;
    }

    public static class Builder {
        private World world;
        private com.badlogic.gdx.physics.box2d.World b2dWorld;
        private TiledMap tiledMap;
        private MapObjects mapObjects;
        private String entityLayerName;
        private float worldScale;
        private String compPackage;
        private String statePackage;
        private String contactListenerPackage;

        private Bag<Class<? extends Component>> autoInitCompClasses;
        public Builder scale(float worldScale) {
            this.worldScale = worldScale;
            return this;
        }
        public Builder world(World world) {
            this.world = world;
            return this;
        }
        public Builder compPackage(String compPackage) {
            this.compPackage = compPackage;
            return this;
        }
        public Builder statePackage(String statePackage) {
            this.statePackage = statePackage;
            return this;
        }
        public Builder contactListenerPackage(String contactListenerPackage) {
            this.contactListenerPackage = contactListenerPackage;
            return this;
        }
        public Builder box2DWorld(com.badlogic.gdx.physics.box2d.World box2DWorld) {
            this.b2dWorld = box2DWorld;
            return this;
        }
        public Builder tiledMap(TiledMap tiledMap){
            this.tiledMap = tiledMap;
            return this;
        }
        public Builder entityLayerName(String entityLayerName) {
            this.entityLayerName = entityLayerName;
            return this;
        }
        public Builder addAutoInitComp(Class<? extends Component> compClass){
            if (autoInitCompClasses == null) {
                autoInitCompClasses = new Bag<>();
            }
            autoInitCompClasses.add(compClass);
            return this;
        }

        public TiledToECS build() {
            if (tiledMap == null) {
                Gdx.app.error(TAG,"tiledMap is null,Confirm if the correct map name has been passed in!");
                return null;
            }
            MapLayers layers = tiledMap.getLayers();
            MapLayer mapLayer = layers.get(entityLayerName);
            if (mapLayer == null){
                Gdx.app.error(TAG,"No layer with this name found:"+entityLayerName);
                return null;
            }
            mapObjects = mapLayer.getObjects();

            TiledToECS instance = new TiledToECS();
            instance.builder = this;
            return instance;
        }
    }
}
