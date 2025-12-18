package org.ltae.ui.inventory;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.ltae.manager.map.serialize.json.EntityData;

public class SlotData {
    public int itemId;
    public String itemName;

    public int stackAmount;
    public int maxStack;

    public TextureRegionDrawable drawable;
    public EntityData entityData;
}
