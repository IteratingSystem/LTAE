package org.ltae.ui.inventory;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.ltae.manager.map.MapManager;
import org.ltae.serialize.data.EntityDatum;

public class SlotDatum {
    private final static String TAG = SlotDatum.class.getSimpleName();

    public int itemId;
    public String itemName;
    public String itemType;

    public int stackAmount;
    public int maxStack;
    public int unitPrice;
    public boolean selected;

    // 存入物品的实体信息,用于快速创建实体
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

    // 用于对比是否是同一个对象
    public boolean itemEquals(SlotDatum slotDatum){
        if (slotDatum.itemId == itemId && itemId != -1) {
            return true;
        }
        return false;
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
