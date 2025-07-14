package org.ltae.event.on;

import com.artemis.Entity;
import com.artemis.World;
import org.ltae.event.InteractiveEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:37
 * @Description 交互事件接收器
 **/
public abstract class OnInteractiveEvent {
    public World world;
    public Entity entity;
    public int entityId;

    public OnInteractiveEvent(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
        entityId = entity.getId();
    }


    void onEvent(InteractiveEvent event) {
    }
}
