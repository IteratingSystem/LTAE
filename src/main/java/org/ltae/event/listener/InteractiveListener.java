package org.ltae.event.listener;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.event.InteractiveEvent;

/**
 * @Auther WenLong
 * @Date 2025/7/2 10:37
 * @Description 交互事件接收器
 **/
public abstract class InteractiveListener {
    public World world;
    public EventSystem eventSystem;
    public TagManager tagManager;
    public Entity entity;
    public int entityId;

    public Entity fromEntity;
    public int fromEntityId;


    public InteractiveListener(Entity entity){
        this.entity = entity;
        world = entity.getWorld();
        entityId = entity.getId();
        eventSystem = world.getSystem(EventSystem.class);
        tagManager = world.getSystem(TagManager.class);
    }

    public void onEvent(InteractiveEvent event) {
        fromEntityId = event.fromId;
        fromEntity = world.getEntity(fromEntityId);
    }

    public String getTag(){
        return getClass().getSimpleName();
    }
}
