package org.ltae.manager.map.serialize.json;


import com.artemis.utils.Bag;


/**
 * @Auther WenLong
 * @Date 2025/7/4 10:41
 * @Description 将实体列表转换为实体数据列表
 **/
public class EntityData extends Bag<EntityDatum> {
    public boolean hasEntityData(EntityDatum entityDatum){
        for (EntityDatum datum : this) {
            if (datum.equals(entityDatum)) {
                return true;
            }
        }
        return false;
    }
}
