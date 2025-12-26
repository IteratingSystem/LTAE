package org.ltae.event;

import net.mostlyoriginal.api.event.common.Event;

/**
 * @Auther WenLong
 * @Date 2025/6/30 10:23
 * @Description 全局事件
 **/
public abstract class TypeEvent implements Event {
    public int type;
    public TypeEvent(int type){
        this.type = type;
    }
}
