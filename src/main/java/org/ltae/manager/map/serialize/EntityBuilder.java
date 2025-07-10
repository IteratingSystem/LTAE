package org.ltae.manager.map.serialize;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.EntitiesJson;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();
    private final EntitySerializer entitySerializer;

    public EntityBuilder(EntitySerializer entitySerializer) {
        this.entitySerializer = entitySerializer;
    }

    public void buildEntities(World world,String mapName) {
        EntitiesJson entitiesJson = entitySerializer.getEntitiesJson(mapName);
        entitySerializer.createEntities(world,entitiesJson);
    }
}
