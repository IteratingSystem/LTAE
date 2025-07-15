package org.ltae.event;

import org.ltae.camera.CameraTarget;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:56
 * @Description 相机事件
 **/
public class MapEvent extends TypeEvent{
    public static final int CHANGE_MAP = 1;
    public String mapName;
    public MapEvent(int type) {
        super(type);
    }
}
