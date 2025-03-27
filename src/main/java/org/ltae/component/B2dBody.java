package org.ltae.component;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.ltae.b2d.CategoryBits;
import org.ltae.b2d.FixData;
import org.ltae.b2d.SensorType;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;
import org.ltae.utils.ShapeUtils;

import java.util.Iterator;


/**
 * @Auther WenLong
 * @Date 2025/2/17 15:38
 * @Description Box2D身体
 **/
public class B2dBody extends Component implements TileCompLoader {
    @TileParam
    public String defType;//动静态类型
    @TileParam
    public boolean defFixed;//是否固定旋转
    @TileParam
    public float linearDamping;//线性阻尼


    public int entityId;
    public World b2dWorld;
    public BodyDef bodyDef;
    public Body body;
    @Override
    public void loader(TileDetails tileDetails) {
        //获取传入参数的属性
        MapObject mapObject = tileDetails.mapObject;
        if (!(mapObject instanceof TiledMapTileMapObject tileMapObject)) {
            return;
        }
        MapProperties props = mapObject.getProperties();
        float posX = props.get("x", float.class);
        float posY = props.get("y", float.class);

        TiledMapTile tile = tileMapObject.getTile();
        MapObjects allObjects = new MapObjects();
        MapObjects objects = tile.getObjects();

        b2dWorld = tileDetails.b2dWorld;
        entityId = tileDetails.entityId;

        //构造关键数据
        bodyDef = new BodyDef();
        bodyDef.fixedRotation = defFixed;
        bodyDef.type = BodyDef.BodyType.valueOf(defType);

        bodyDef.position.set(tileDetails.worldScale*posX, tileDetails.worldScale*posY);
        body = b2dWorld.createBody(bodyDef);
        body.setLinearDamping(linearDamping);

        //tiled原本的对象
        for (MapObject object : objects) {
            allObjects.add(object);
        }

        //动画帧中的形状对象
        TiledMapTile tiledMapTile = tileDetails.tiledMapTile;
        int tileId = tiledMapTile.getId();
        for (TiledMapTileSet tileSet : tileDetails.tiledMap.getTileSets()) {
            if (tileSet.getTile(tileId) != tiledMapTile) {
                continue;
            }
            Iterator<TiledMapTile> tileSetItr = tileSet.iterator();
            while (tileSetItr.hasNext()){
                TiledMapTile oneTile = tileSetItr.next();
                if (!(oneTile instanceof AnimatedTiledMapTile animatedTile)){
                    continue;
                }
                StaticTiledMapTile[] frameTiles = animatedTile.getFrameTiles();
                for (StaticTiledMapTile frameTile : frameTiles) {
                    MapObjects frameTileObjects = frameTile.getObjects();
                    for (MapObject frameTileObject : frameTileObjects) {
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

            Shape shape = ShapeUtils.getShapeByMapObject(object, tileDetails.worldScale);

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

            Fixture fixture = body.createFixture(fixtureDef);
            FixData fixData = new FixData();
            fixData.entityId = entityId;
            fixData.entity = tileDetails.entity;
            fixData.sensorType = SensorType.valueOf(sensorType);
            fixture.setUserData(fixData);
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

            if (fixtureA.getUserData() instanceof FixData fixDataA
                && fixtureB.getUserData() instanceof FixData fixDataB
                && fixDataA.entityId == fixDataB.entityId){
                continue;
            }


            if (fixtureA.getUserData() instanceof FixData fixData
                && fixData.entityId == entityId
                && fixData.sensorType == SensorType.ON_FLOOR
                && fixtureB.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    return true;
            }

            if (fixtureB.getUserData() instanceof FixData fixData
                && fixData.entityId == entityId
                && fixData.sensorType == SensorType.ON_FLOOR
                && fixtureA.getBody().getType() == BodyDef.BodyType.StaticBody) {
                return true;
            }
        }
        return false;
    }
}
