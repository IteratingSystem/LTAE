package org.ltae.event;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.maps.MapObject;
import org.ltae.manager.map.serialize.json.EntityData;
import org.ltae.manager.map.serialize.json.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/6/30 10:33
 * @Description 实体事件
 **/
public class EntityEvent extends TypeEvent {
    public static final int CREATE_ENTITY = 1;
    public static final int CREATE_PREFAB = 2;
    public static final int GET_MAP_OBJECT = 3;
    public static final int CREATE_ALL = 4;
    public static final int ADD_AUTO_COMP = 5;
    public static final int SERIALIZER_ENTITIES = 6;
    //传要删除的entityId
    public static final int DELETE_ENTITY = 7;
    public static final int DELETE_ALL = 8;
    public static final int DEL_AND_CREATE_ALL = 9;
    public static final int FILTER_DEL_ALL = 10;
    //传entityDatum
    public static final int BUILD_ENTITY = 11;
    //传entityData
    public static final int BUILD_ENTITIES = 12;

    public float x;
    public float y;
    public MapObject mapObject;
    public String name;
    public Entity entity;
    public int entityId = -1;

    //序列化后的字符串,用于SERIALIZER_ENTITIES序列化后接受结果
    public String serializerEntitiesStr;
    //实体标签列表,用于FILTER_DEL_ALL过滤不要删除的实体
    public String[] entityTags;
    public EntityData entityData;
    public EntityDatum entityDatum;

    public Class<? extends Component> compClass;

    public EntityEvent(int type) {
        super(type);
    }
}
