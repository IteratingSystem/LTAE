package org.ltae.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.ltae.box2d.*;
import org.ltae.box2d.listener.EcsContactListener;
import org.ltae.box2d.setup.FixtureSetup;
import org.ltae.system.B2dSystem;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.TileParam;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.ShapeUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;


/**
 * @Auther WenLong
 * @Date 2025/2/17 15:38
 * @Description Box2D身体
 **/
public class B2dBody extends Component implements ComponentLoader {
    private final static String TAG = B2dBody.class.getSimpleName();
    @TileParam
    public String defType;//动静态类型
    @TileParam
    public boolean defFixed;//是否固定旋转
    @TileParam
    public float linearDamping;//线性阻尼


    public Bag<FixtureSetup> keyframeFixSetups;
    public int entityId;
    public World b2dWorld;
    public BodyDef bodyDef;
    public Body body;

    //创建阶段是否翻转,用于给需要随时创建和删除的形状标记创建时的初始位置
    //目前使用的地方未KeyframeShapeSystem
    //需要翻转与当前翻转状态
    public boolean needFlipX = false;

    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
        //获取传入参数的属性
        MapObject mapObject = entityDetails.mapObject;
        if (!(mapObject instanceof TiledMapTileMapObject tileMapObject)) {
            return;
        }
        MapProperties props = mapObject.getProperties();
        float posX = props.get("x", float.class);
        float posY = props.get("y", float.class);

        TiledMapTile tile = tileMapObject.getTile();
        MapObjects allObjects = new MapObjects();
        MapObjects objects = tile.getObjects();

        com.artemis.World world = systemDetails.world;
        B2dSystem b2dSystem = world.getSystem(B2dSystem.class);
        b2dWorld = b2dSystem.box2DWorld;
        entityId = entityDetails.entityId;
        Entity entity = entityDetails.entity;

        //构造关键数据
        bodyDef = new BodyDef();
        bodyDef.fixedRotation = defFixed;
        bodyDef.type = BodyDef.BodyType.valueOf(defType);
        keyframeFixSetups = new Bag<>();

        float worldScale = systemDetails.worldScale;
        bodyDef.position.set(worldScale*posX, worldScale*posY);
        body = b2dWorld.createBody(bodyDef);
        body.setLinearDamping(linearDamping);

        //tiled原本的对象
        for (MapObject object : objects) {
            allObjects.add(object);
        }

        //动画帧中的形状对象
        TiledMapTile tiledMapTile = entityDetails.tiledMapTile;
        int tileId = tiledMapTile.getId();
        TiledMap tiledMap = systemDetails.tiledMap;
        for (TiledMapTileSet tileSet : tiledMap.getTileSets()) {
            if (tileSet.getTile(tileId) != tiledMapTile) {
                continue;
            }
            Iterator<TiledMapTile> tileSetItr = tileSet.iterator();
            while (tileSetItr.hasNext()){
                TiledMapTile oneTile = tileSetItr.next();
                if (!(oneTile instanceof AnimatedTiledMapTile animatedTile)){
                    continue;
                }
                MapProperties allProps = animatedTile.getProperties();
                if (!allProps.containsKey("TileAnimation")){
                    continue;
                }
                MapProperties aniProp = allProps.get("TileAnimation", MapProperties.class);
                if (!aniProp.containsKey("name")){
                    continue;
                }

                StaticTiledMapTile[] frameTiles = animatedTile.getFrameTiles();
                String aniName = aniProp.get("name","", String.class);
                for (int i = 0; i < frameTiles.length; i++) {
                    StaticTiledMapTile frameTile = frameTiles[i];
                    MapObjects frameTileObjects = frameTile.getObjects();
                    for (MapObject frameTileObject : frameTileObjects) {
                        MapProperties properties = frameTileObject.getProperties();
                        if (!properties.containsKey("FixDef")) {
                            continue;
                        }
                        MapProperties fixDefProps = properties.get("FixDef",MapProperties.class);
                        fixDefProps.put("keyframeIndex",i);
                        fixDefProps.put("aniName",aniName);
                        allObjects.add(frameTileObject);
                    }
                }

            }
        }

        //将所有对象中是形状的转换成shape
        for (MapObject object : allObjects) {
            MapProperties shapeProps = object.getProperties();
            if (!shapeProps.containsKey("FixDef")) {
                continue;
            }
            MapProperties fixDefProps = shapeProps.get("FixDef",MapProperties.class);
            float density = fixDefProps.get("density", float.class);
            float friction = fixDefProps.get("friction", float.class);
            float restitution = fixDefProps.get("restitution", float.class);
            boolean isSensor = fixDefProps.get("isSensor", boolean.class);
            String sensorType = fixDefProps.get("sensorType", String.class);
            String categoryBit = fixDefProps.get("categoryBit", String.class);
            String maskBits = fixDefProps.get("maskBits", String.class);
            String listenerSimpleName = fixDefProps.get("listenerSimpleName", String.class);
            //帧动画数据
            String aniName = fixDefProps.get("aniName","", String.class);
            int keyframeIndex = fixDefProps.get("keyframeIndex",0, Integer.class);

            //创建监听器
            EcsContactListener ecsContactListener = null;
            if (listenerSimpleName != null && !listenerSimpleName.isEmpty()) {
                String contactListenerPackage = systemDetails.b2dListenerPkg;
                if (contactListenerPackage == null || contactListenerPackage.isEmpty()){
                    Gdx.app.error(TAG,"contactListenerPackage is empty,please set contactListenerPackage by LtaeBuilder");
                }else {
                    String className = contactListenerPackage + "." + listenerSimpleName;
                    Class<EcsContactListener> contactClass;
                    try {
                        contactClass = (Class<EcsContactListener>) Class.forName(className);
                        ecsContactListener = contactClass.getConstructor(Entity.class).newInstance(entity);
                    } catch (ClassNotFoundException e) {
                        Gdx.app.error(TAG,"Failed to get class with name:"+className);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            Shape shape = ShapeUtils.getShapeByMapObject(object, worldScale);

            if (shape == null){
                continue;
            }

            Filter filter = new Filter();
            filter.categoryBits = CategoryBits.valueOf(categoryBit).getBit();
            filter.maskBits = CategoryBits.getMask(maskBits);


            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            fixtureDef.isSensor = isSensor;
            fixtureDef.filter.set(filter);
            fixtureDef.shape = shape;

            //不是动画帧中的形状时,直接创建出来
            if ("".equals(aniName)) {
                Fixture fixture = body.createFixture(fixtureDef);

                DefFixData defFixData = new DefFixData();
                defFixData.entityId = entityId;
                defFixData.entity = entityDetails.entity;
                defFixData.sensorType = SensorType.valueOf(sensorType);
                defFixData.listener = ecsContactListener;

                fixture.setUserData(defFixData);

                continue;
            }
            //否则先保存起来,后期触发的时候再创建
            KeyframeShapeData keyframeShapeData = new KeyframeShapeData();
            keyframeShapeData.entityId = entityId;
            keyframeShapeData.entity = entity;
            keyframeShapeData.sensorType = SensorType.valueOf(sensorType);
            keyframeShapeData.listener = ecsContactListener;
            keyframeShapeData.aniName = aniName;
            keyframeShapeData.keyframeIndex = keyframeIndex;
            FixtureSetup fixtureSetup = new FixtureSetup(fixtureDef,keyframeShapeData);
            keyframeFixSetups.add(fixtureSetup);
        }
    }
    public boolean isOnFloor(){
        Array<Contact> contactList = b2dWorld.getContactList();
        for (Contact contact : contactList) {
            if (!contact.isTouching()) {
                continue;
            }

            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            if (fixtureA.getUserData() instanceof DefFixData defFixDataA
                && fixtureB.getUserData() instanceof DefFixData defFixDataB
                && defFixDataA.entityId == defFixDataB.entityId){
                continue;
            }


            if (fixtureA.getUserData() instanceof DefFixData defFixData
                && defFixData.entityId == entityId
                && defFixData.sensorType == SensorType.ON_FLOOR
                && fixtureB.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    return true;
            }

            if (fixtureB.getUserData() instanceof DefFixData defFixData
                && defFixData.entityId == entityId
                && defFixData.sensorType == SensorType.ON_FLOOR
                && fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody) {
                return true;
            }
        }
        return false;
    }

    /**
     * 传入用户对象获取keyframeFixSetups中对应的那个FixSetup
     * @param keyframeShapeData
     * @return
     */
    public FixtureSetup getKeyframeFixSetup(KeyframeShapeData keyframeShapeData){
        for (FixtureSetup keyframeFixSetup : keyframeFixSetups) {
            if (keyframeFixSetup.fixtureData != keyframeShapeData) {
                continue;
            }
            return keyframeFixSetup;
        }
        return null;
    }
    /**
     * 传入用户对象获取keyframeFixSetups中对应的那个FixSetup中的FixtureDef
     * @param keyframeShapeData
     * @return
     */
    public FixtureDef getKeyframeFixDef(KeyframeShapeData keyframeShapeData){
        FixtureSetup keyframeFixSetup = getKeyframeFixSetup(keyframeShapeData);
        if(keyframeFixSetup == null){
            return null;
        }
        return keyframeFixSetup.FixtureDef;
    }

    /**
     * 左右翻转,假设角色左右翻转,所以他的Body也要左右翻转;
     * 这里没有用强制性的翻转方向,而是将Body内部的所有矩形和圆形移动到翻转后的位置来模拟的翻转;
     * 翻转逻辑会忽略动画帧形状中的翻转情况,因为动画帧形状的翻转状态会在创建时判断,通过needFlipX属性来判断是否需要翻转;
     * @param regionWidth
     */
    public void flipX(float regionWidth){
        Array<Fixture> fixtureList = body.getFixtureList();
        for (Fixture fixture : fixtureList) {
            Object userData = fixture.getUserData();
            //不需要处理动画帧中的形状,因为其在创建阶段翻转
            if (userData instanceof KeyframeShapeData keyframeShapeData) {
                continue;
            }
            ShapeUtils.flipX(fixture.getShape(),regionWidth);
        }
    }
}
