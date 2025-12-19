package org.ltae.ui.inventory;

import com.artemis.ComponentManager;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.component.Inert;
import org.ltae.component.Render;
import org.ltae.event.EntityEvent;
import org.ltae.manager.map.serialize.json.EntityDatum;

public class SlotData {
    public int itemId;
    public String itemName;

    public int stackAmount;
    public int maxStack;
    public EntityDatum entityDatum;

    public TextureRegionDrawable getDrawable(World world) {
        if (entityDatum == null){
            return null;
        }

        //暂时先用这种方式获取图片
        EntityEvent entityEvent = new EntityEvent(EntityEvent.BUILD_ENTITY);
        entityEvent.entityDatum = entityDatum;
        EventSystem eventSystem = world.getSystem(EventSystem.class);
        eventSystem.dispatch(entityEvent);
        Render render = entityEvent.entity.getComponent(Render.class);
        ComponentMapper<Inert> mInert = world.getMapper(Inert.class);
        mInert.create(entityEvent.entity);

        return new TextureRegionDrawable(render.keyframe);
    }
}
