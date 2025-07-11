package org.ltae.event.on;

import com.artemis.Entity;
import com.artemis.World;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.event.InterEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:37
 * @Description 交互事件接收器
 **/
public abstract class OnInterEvent {
    public World world;
    public Entity entity;
    public int entityId;

    public OnInterEvent(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
    }

    @Subscribe
    public void onEvent(InterEvent event){
        if (event.toId != entityId){
            return;
        }
    }
}
