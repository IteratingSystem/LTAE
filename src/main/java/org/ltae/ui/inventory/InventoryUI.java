package org.ltae.ui.inventory;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import org.ltae.ui.BaseEcsUI;

/**
 * 库存UI
 */
public class InventoryUI extends BaseEcsUI {
    private DragAndDrop dragAndDrop;
    private SlotUI.InventorySlotStyle inventorySlotStyle;
    private SlotUI[][] slots;
    private Array<Array<SlotDatum>> slotData;
    //拖拽放下后,不处理拖拽逻辑的来源Actor,也就是拖拽来源黑名单
    private Array<Actor> dragBlacklist;
    private int slotSize = 32;
    //是否能拖到地上
    private boolean canDragStop = true;
    //归属者(实体id)
    private int owner;
    public InventoryUI(World world, String inventorySlotStyleName) {
        super(world);
        inventorySlotStyle = skin.get(inventorySlotStyleName, SlotUI.InventorySlotStyle.class);
        dragBlacklist = new Array<>();
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragTime(100);
    }
    public InventoryUI(World world,DragAndDrop dragAndDrop,String inventorySlotStyleName) {
        super(world);
        this.dragAndDrop = dragAndDrop;
        inventorySlotStyle = skin.get(inventorySlotStyleName, SlotUI.InventorySlotStyle.class);
        dragBlacklist = new Array<>();
    }

    public void setCanDragStop(boolean canDragStop) {
        this.canDragStop = canDragStop;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getOwner() {
        return owner;
    }

    public void setSlotSize(int slotSize) {
        this.slotSize = slotSize;
    }

    public void addDragBlack(Actor actor){
        dragBlacklist.add(actor);
    }
    public void rmDragBlack(Actor actor){
        dragBlacklist.removeValue(actor,true);
    }
    public void rebuild(Array<Array<SlotDatum>> slotData) {
        this.slotData = slotData;
        clear();
        int rows = slotData.size;
        int cols = slotData.get(0).size;
        slots = new SlotUI[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                SlotUI slotUI = new SlotUI(world,inventorySlotStyle);
                add(slotUI).size(slotSize);
                slotUI.setPosForInventory(r,c);
                slots[r][c] = slotUI;
                enableDrag(slotUI);

                SlotDatum slotDatum = slotData.get(r).get(c);
                slotUI.setSlotData(slotDatum);
            }
            row();
        }
    }

    public Array<Array<SlotDatum>> getSlotData() {
        return slotData;
    }

    public void setSlotData(Array<Array<SlotDatum>> slotData) {
        this.slotData = slotData;
    }

    public SlotUI getSlot(int x,int y){
        if (slots == null) {
            return null;
        }
        return slots[x][y];
    }

    /* ===== 拖拽能力 ===== */
    private void enableDrag(SlotUI slot) {
        dragAndDrop.addSource(new DragAndDrop.Source(slot) {
            //按住开始拖拽
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                return onDragStart(slot);   // 交给子类
            }
            //拖到空地
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (!canDragStop) {
                    return;
                }
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
                if (dragBlacklist.contains(source.getActor().getParent(),true)) {
                    return;
                }
                onDrop(source,payload,getActor());

            }

        });
    }

    /* ===== 子类唯一要关心的两个钩子 ===== */
    //按住开始拖拽
    public DragAndDrop.Payload onDragStart(SlotUI slot) {
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


        //交换后记录上一个库存容器
        Group fromParent = source.getActor().getParent();
        SlotUI fromSlot = (SlotUI)source.getActor();

        Group targetParent = targetActor.getParent();
        SlotUI targetSlot = (SlotUI)targetActor;

        if (fromParent != targetParent
            && fromParent instanceof InventoryUI fromInventory
            && targetParent instanceof InventoryUI targetInventory){
            fromSlot.setFromInventory(fromInventory);
            targetSlot.setFromInventory(targetInventory);
        }

        swapData(fromSlot,targetSlot);
    }

    public void swapData(SlotUI fromSlot,SlotUI targetSlot){
        SlotDatum swapData = fromSlot.getSlotData();
        fromSlot.setSlotData(targetSlot.getSlotData());
        targetSlot.setSlotData(swapData);
    }
}
