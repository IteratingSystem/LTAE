package org.ltae.manager.map.serialize;

import com.artemis.World;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.manager.map.serialize.data.EntityData;
import org.ltae.manager.map.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();
    public static void buildEntities(World world,String mapName) {
        EntityData entityData = WorldStateManager.getInstance().getEntityData(mapName);
        buildEntities(world, entityData);
    }
    public static void buildEntities(World world, EntityData entityData) {
        EntitySerializer.buildEntities(world, entityData);
    }
    public static int buildEntity(World world, EntityDatum entityDatum) {
        return EntitySerializer.buildEntity(world, entityDatum);
    }
}
