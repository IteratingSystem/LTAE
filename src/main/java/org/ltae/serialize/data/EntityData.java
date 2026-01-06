package org.ltae.serialize.data;

import com.badlogic.gdx.utils.Array;


/**
 * @Auther WenLong
 * @Date 2025/7/4 10:41
 * @Description 将实体列表转换为实体数据列表
 **/
public class EntityData extends Array<EntityDatum> {
    public final static int FROM_MAP = 0;
    public final static int FROM_WORLD = 1;

    public boolean hasEntityData(EntityDatum entityDatum){
        for (EntityDatum datum : this) {
            if (datum.equals(entityDatum)) {
                return true;
            }
        }
        return false;
    }
    public void setDataFrom(int dataFrom){
        for (EntityDatum datum : this) {
            datum.dataFrom = dataFrom;
        }
    }
}
