package org.ltae.manager.map.serialize.json;

import com.artemis.utils.Bag;

/**
 * @Auther WenLong
 * @Date 2025/7/4 10:41
 * @Description
 **/
public class EntitiesBag {
    public Bag<EntityData> entities;
    public boolean hasEntityData(EntityData entityData){
        if (entities == null) {
            return false;
        }
        for (EntityData entity : entities) {
            if (entity.equals(entityData)) {
                return true;
            }
        }
        return false;
    }
}
