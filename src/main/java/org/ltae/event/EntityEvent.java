package org.ltae.event;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.maps.MapObject;
import org.ltae.manager.map.serialize.json.EntitiesBag;

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
    public static final int DELETE_ENTITY = 7;

    public float x;
    public float y;
    public MapObject mapObject;
    public String name;
    public Entity entity;
    public int entityId;
    public String entitiesStr;
    public EntitiesBag entitiesBag;
    public Class<? extends Component> compClass;

    public EntityEvent(int type) {
        super(type);
    }
}
