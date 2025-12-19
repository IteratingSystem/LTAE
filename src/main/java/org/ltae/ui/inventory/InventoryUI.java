package org.ltae.ui.inventory;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import org.ltae.ui.BaseEcsUI;

/**
 * 库存UI
 */
public class InventoryUI extends BaseEcsUI {
    private DragAndDrop dragAndDrop;
    private InventorySlot.InventorySlotStyle inventorySlotStyle;
    private InventorySlot[][] slots;
    private SlotData[][] slotData;

    public InventoryUI(World world, String inventorySlotStyleName) {
        super(world);
        inventorySlotStyle = skin.get(inventorySlotStyleName, InventorySlot.InventorySlotStyle.class);
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragTime(100);
    }
    public InventoryUI(World world,DragAndDrop dragAndDrop,String inventorySlotStyleName) {
        super(world);
        this.dragAndDrop = dragAndDrop;
        inventorySlotStyle = skin.get(inventorySlotStyleName, InventorySlot.InventorySlotStyle.class);
    }

    /* ===== 一次性画好所有格子并打开拖拽 ===== */
    public void rebuild(SlotData[][] slotData) {
        this.slotData = slotData;
        clear();
        int rows = slotData.length;
        int cols = slotData[0].length;
        slots = new InventorySlot[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                InventorySlot inventorySlot = new InventorySlot(world,inventorySlotStyle);
                add(inventorySlot).size(64);
                inventorySlot.setPosForInventory(r,c);
                slots[r][c] = inventorySlot;
                enableDrag(inventorySlot);

                SlotData slotDatum = slotData[r][c];
                inventorySlot.setSlotData(slotDatum);
            }
            row();
        }
    }

    public SlotData[][] getSlotData() {
        return slotData;
    }

    public void setSlotData(SlotData[][] slotData) {
        this.slotData = slotData;
    }

    /* ===== 拖拽能力 ===== */
    private void enableDrag(InventorySlot slot) {
        dragAndDrop.addSource(new DragAndDrop.Source(slot) {
            //按住开始拖拽
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                return onDragStart(slot);   // 交给子类
            }
            //拖到空地，回滚
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                onDragStop(event, x, y, pointer, payload, target);
            }
        });

        dragAndDrop.addTarget(new DragAndDrop.Target(slot) {

            //拖拽后触碰到
            @Override
            public boolean drag(DragAndDrop.Source source,
                                DragAndDrop.Payload payload,
                                float x, float y, int pointer) {
                return !slot.isDisabled();
            }
            //拖拽后松开按钮
            @Override
            public void drop(DragAndDrop.Source source,
                             DragAndDrop.Payload payload,
                             float x, float y, int pointer) {
                onDrop(source,payload,getActor());

            }

        });
    }

    /* ===== 子类唯一要关心的两个钩子 ===== */
    //按住开始拖拽
    public DragAndDrop.Payload onDragStart(InventorySlot slot) {
        DragAndDrop.Payload payload = new DragAndDrop.Payload();
        Image dragActor = new Image(slot.getIcon().getDrawable());
        dragActor.setScale(4f);
        payload.setDragActor(dragActor);
        return payload;
    }
    //拖到空地丢弃
    public void onDragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target){
    }
    //拖拽后松开按钮
    public void onDrop(DragAndDrop.Source source,
                          DragAndDrop.Payload payload,
                          Actor targetActor) {
        if (slots == null) {
            Gdx.app.debug(getTag(),"Failed to swap item,'slots' is null!");
            return;
        }
        InventorySlot fromSlot = (InventorySlot)source.getActor();
        InventorySlot targetSlot = (InventorySlot)targetActor;

        SlotData swapData = fromSlot.getSlotData();
        fromSlot.setSlotData(targetSlot.getSlotData());
        targetSlot.setSlotData(swapData);
    }
}
