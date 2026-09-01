package org.worldloom.serialize;

import com.artemis.World;
import org.worldloom.manager.map.GameSnapshotManager;
import org.worldloom.serialize.data.EntityData;
import org.worldloom.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();

    public static void buildEntities(World world,String mapName) {
        EntityData entityData = GameSnapshotManager.getInstance().getEntityData(mapName);
        buildEntities(world, entityData);
    }
    public static void buildEntities(World world, EntityData entityData) {
        EntitySerializer.buildEntities(world, entityData);
    }
    public static int buildEntity(World world, EntityDatum entityDatum) {
        return EntitySerializer.buildEntity(world, entityDatum);
    }
}
