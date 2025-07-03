package org.ltae.tiled;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();
    private ComponentInitializer componentInitializer;
    private SystemDetails systemDetails;
    public EntityBuilder(SystemDetails systemDetails) {
        this.systemDetails = systemDetails;
        this.componentInitializer = new ComponentInitializer(systemDetails);
    }

    public Entity createEntity(MapObject mapObject) {
        World world = systemDetails.world;
        int entityId = world.create();
        Entity entity = world.getEntity(entityId);
        TiledMapTile tiledMapTile = null;
        if (mapObject instanceof TiledMapTileMapObject tileMapObject) {
            tiledMapTile = tileMapObject.getTile();
        }
        EntityDetails entityDetails = new EntityDetails();
        entityDetails.entityId = entityId;
        entityDetails.mapObject = mapObject;
        entityDetails.tiledMapTile = tiledMapTile;
        componentInitializer.createAndInitAll(entityDetails);
        return entity;
    }

    public void createAllEntity(TiledMap tiledMap,String layerName){
        if (tiledMap == null){
            Gdx.app.error(TAG,"tiledMap is null");
            return;
        }
        MapLayers layers = tiledMap.getLayers();
        if (layers == null) {
            Gdx.app.error(TAG,"Failed to 'createAllEntity','tiledMap.getLayers()' is null");
            return;
        }
        MapLayer layer = layers.get(layerName);
        if (layer == null){
            Gdx.app.error(TAG,"Failed to 'createAllEntity','layer' is null,layer'name: "+layerName);
            return;
        }

        MapObjects objects = layer.getObjects();
        for (MapObject object : objects) {
            createEntity(object);
        }
    }
    public void createAllEntity(){
        TiledMap tiledMap = systemDetails.tiledMap;
        String entityLayer = systemDetails.entityLayer;
        createAllEntity(tiledMap,entityLayer);
    }


}
