package org.ltae.event;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.maps.MapObject;
import org.ltae.manager.map.serialize.json.EntitiesJson;

/**
 * @Auther WenLong
 * @Date 2025/6/30 10:33
 * @Description 创建实体
 **/
public class EntityFactoryEvent extends TypeEvent {
    public static final int CREATE_ENTITY = 1;
    public static final int CREATE_PREFAB = 2;
    public static final int GET_MAP_OBJECT = 3;
    public static final int CREATE_ALL = 4;
    public static final int ADD_AUTO_COMP = 5;
    public static final int SERIALIZER_ENTITIES = 6;

    public float x;
    public float y;
    public MapObject mapObject;
    public String name;
    public Entity entity;
    public String entitiesStr;
    public EntitiesJson entitiesJson;
    public Class<? extends Component> compClass;

    public EntityFactoryEvent(int type) {
        super(type);
    }
}
