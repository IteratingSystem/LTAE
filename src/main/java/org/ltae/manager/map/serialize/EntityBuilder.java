package org.ltae.manager.map.serialize;

import com.artemis.World;
import org.ltae.manager.map.WorldStateManager;
import org.ltae.manager.map.serialize.json.EntityDataList;

/**
 * @Auther WenLong
 * @Date 2025/5/13 10:14
 * @Description
 **/
public class EntityBuilder {
    private final static String TAG = EntityBuilder.class.getSimpleName();
    public static void buildEntities(World world,String mapName) {
        EntityDataList entityDataList = WorldStateManager.getInstance().getEntityDataList(mapName);
        buildEntities(world, entityDataList);
    }
    public static void buildEntities(World world, EntityDataList entityDataList) {
        EntitySerializer.createEntities(world, entityDataList);
    }
}
