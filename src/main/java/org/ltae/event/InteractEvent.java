package org.ltae.event;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:41
 * @Description 交互事件
 **/
public class InteractEvent extends TypeEvent {
    public final static int SHORT_PRESS = 1;
    public final static int LONG_PRESS = 2;

    public int fromId;
    public int toId;

    public InteractEvent(int fromId, int toId) {
        this(fromId,toId,SHORT_PRESS);
    }

    public InteractEvent(int fromId, int toId, int type) {
        super(type);
        this.fromId = fromId;
        this.toId = toId;
    }
}
