package org.ltae.event.listener;

import com.artemis.Entity;
import com.artemis.World;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.event.InteractiveEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:37
 * @Description 交互事件接收器
 **/
public abstract class EntityEventListener {
    public World world;
    public EventSystem eventSystem;
    public Entity entity;
    public int entityId;


    public EntityEventListener(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
        entityId = entity.getId();
        eventSystem = world.getSystem(EventSystem.class);
    }


    void onEvent(InteractiveEvent event) {
    }
}
