package org.ltae.event;

import org.ltae.camera.CameraTarget;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:56
 * @Description 相机事件
 **/
public class CameraEvent extends TypeEvent{
    public static final int SET_TARGET = 1;
    public static final int RESIZE = 2;
    public CameraTarget target;
    public int width;
    public int height;

    public CameraEvent(int type) {
        super(type);
    }
}
