package org.ltae.event;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:56
 * @Description 相机事件
 **/
public class B2dEvent extends TypeEvent{
    public static final int DELETE_ALL_BODIES = 1;
    public B2dEvent(int type) {
        super(type);
    }
}
