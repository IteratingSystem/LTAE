package org.ltae.event.on;

import com.artemis.Entity;
import com.artemis.World;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.event.EntityEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:37
 * @Description 实体事件接收器
 **/
public abstract class OnEntityEvent {
    public World world;
    public Entity entity;
    public int entityId;

    public OnEntityEvent(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
    }

    @Subscribe
    public void onEvent(EntityEvent event){
        if (event.toId != entityId){
            return;
        }
    }
}
