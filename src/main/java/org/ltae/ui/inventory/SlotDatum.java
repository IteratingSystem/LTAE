package org.ltae.ui.inventory;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.component.Inert;
import org.ltae.component.Render;
import org.ltae.event.EntityEvent;
import org.ltae.manager.map.MapManager;
import org.ltae.serialize.data.EntityData;
import org.ltae.serialize.data.EntityDatum;

public class SlotDatum {
    private final static String TAG = SlotDatum.class.getSimpleName();

    public int itemId;
    public String itemName;
    public String itemType;

    public int stackAmount;
    public int maxStack;
    public int unitPrice;

    public EntityDatum entityDatum;

    public void copy(SlotDatum fromSlotDatum){
        this.itemId = fromSlotDatum.itemId;
        this.itemName = fromSlotDatum.itemName;
        this.stackAmount = fromSlotDatum.stackAmount;
        this.maxStack = fromSlotDatum.maxStack;
        this.entityDatum = fromSlotDatum.entityDatum;
        this.unitPrice = fromSlotDatum.unitPrice;
        this.itemType = fromSlotDatum.itemType;
    }
    public void exchange(SlotDatum slotDatum){
        SlotDatum newSlotDatum = new SlotDatum();
        newSlotDatum.copy(this);
        this.copy(slotDatum);
        slotDatum.copy(newSlotDatum);
    }
    public boolean isBlank(){
        return stackAmount == 0 || entityDatum == null;
    }
    public TextureRegionDrawable getDrawable(World world) {
        if (entityDatum == null){
            return null;
        }


        MapManager mapManager = MapManager.getInstance();
        String fromMap = entityDatum.fromMap;
        MapObject mapObject = mapManager.getMapObject(fromMap,entityDatum.mapObjectId);
        TextureRegion textureRegion = null;
        if (mapObject instanceof TextureMapObject textureMapObject) {
            textureRegion = textureMapObject.getTextureRegion();
        }else {
            Gdx.app.error(TAG,"Failed to getDrawable(),mapObject is not instanceof TextureMapObject,not has textureRegion!!");
            return null;
        }
        return new TextureRegionDrawable(textureRegion);
    }
}
