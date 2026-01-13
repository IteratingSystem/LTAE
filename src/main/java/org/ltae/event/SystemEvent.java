package org.ltae.event;

/**
 * @author: SystemEvent
 * @date: 2026/1/13
 * @description: 系统事件
 */

public class SystemEvent extends TypeEvent {
    public final static int RESTORE_PROPS = 0;
    public SystemEvent(int type) {
        super(type);
    }
}
