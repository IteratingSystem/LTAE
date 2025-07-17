package org.ltae.manager.map.serialize;

import com.artemis.World;
import org.ltae.manager.map.MapManager;
import org.ltae.manager.map.serialize.json.EntitiesBag;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();
    public static void buildEntities(World world,String mapName) {
//        EntitiesBag entitiesBag = EntitySerializer.getEntitiesJson(mapName);
        EntitiesBag entitiesBag = MapManager.getInstance().getEntities(mapName);
        buildEntities(world, entitiesBag);
    }
    public static void buildEntities(World world, EntitiesBag entitiesBag) {
        EntitySerializer.createEntities(world, entitiesBag);
    }
}
