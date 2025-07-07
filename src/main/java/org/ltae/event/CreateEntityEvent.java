package org.ltae.event;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.maps.MapObject;

/**
 * @Auther WenLong
 * @Date 2025/6/30 10:33
 * @Description 创建实体
 **/
public class CreateEntityEvent extends TypeEvent {
    public static final int CREATE_ENTITY = 1;
    public static final int CREATE_PREFAB = 2;
    public static final int GET_MAP_OBJECT = 3;
    public static final int CREATE_ALL = 4;
    public static final int ADD_AUTO_COMP = 5;

    public float x;
    public float y;
    public MapObject mapObject;
    public String name;
    public Entity entity;
    public Class<? extends Component> compClass;

    public CreateEntityEvent(int type) {
        super(type);
    }
}
