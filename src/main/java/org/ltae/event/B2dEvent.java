package org.ltae.event;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:56
 * @Description 相机事件
 **/
public class B2dEvent extends TypeEvent{
    public static final int DEL_TILE_COLLIDER = 1;
    public static final int CREATE_TILE_COLLIDER = 2;
    public B2dEvent(int type) {
        super(type);
    }
}
