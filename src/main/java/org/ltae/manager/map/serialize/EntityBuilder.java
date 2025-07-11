package org.ltae.manager.map.serialize;

import com.artemis.World;
import org.ltae.manager.map.serialize.json.EntitiesBag;

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
        EntitiesBag entitiesBag = entitySerializer.getEntitiesJson(mapName);
        buildEntities(world, entitiesBag);
    }
    public void buildEntities(World world, EntitiesBag entitiesBag) {
        entitySerializer.createEntities(world, entitiesBag);
    }
}
