package org.ltae.event;

import net.mostlyoriginal.api.event.common.Event;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:41
 * @Description 实体事件
 **/
public class EntityEvent extends TypeEvent {
    public int fromId;
    public int toId;

    public EntityEvent(int fromId,int toId,int type) {
        super(type);
    }
}
