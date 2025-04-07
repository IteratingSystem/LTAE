package org.ltae.box2d;

import com.artemis.Entity;
import org.ltae.box2d.listener.EcsContactListener;


/**
 * @Auther WenLong
 * @Date 2025/2/18 11:03
 * @Description 形状夹具中可以传入此对象用于描述一些业务信息
 **/
public class DefFixData {
    public int entityId;
    public Entity entity;
    public SensorType sensorType;
    public EcsContactListener listener;
}
