package org.ltae.event;

import com.badlogic.gdx.maps.MapObject;

/**
 * @Auther WenLong
 * @Date 2025/6/30 10:33
 * @Description 创建实体
 **/
public class EntityEvent extends TypeEvent {
    public static final int CREATE_ENTITY = 1;
    public static final int CREATE_PREFAB = 2;
    public static final int GET_MAP_OBJECT = 3;

    public float x;
    public float y;
    public MapObject mapObject;
    public String name;

    public EntityEvent(int type) {
        super(type);
    }
}
