package org.ltae.event;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:41
 * @Description 交互事件
 **/
public class InteractiveEvent extends TypeEvent {
    public int fromId;
    public int toId;

    public InteractiveEvent(int fromId, int toId, int type) {
        super(type);
        this.fromId = fromId;
        this.toId = toId;
    }
}
