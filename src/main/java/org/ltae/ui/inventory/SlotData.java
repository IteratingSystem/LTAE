package org.ltae.ui.inventory;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.ltae.component.Render;
import org.ltae.manager.map.serialize.json.EntityDatum;

public class SlotData {
    public int itemId;
    public String itemName;

    public int stackAmount;
    public int maxStack;
    public EntityDatum entityDatum;

    public TextureRegionDrawable getDrawable(World world) {
        Entity entity = world.getEntity(entityDatum.entityId);
        Render render = entity.getComponent(Render.class);
        return new TextureRegionDrawable(render.keyframe);
    }
}
