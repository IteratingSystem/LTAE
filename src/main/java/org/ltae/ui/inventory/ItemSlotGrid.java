package org.ltae.ui.inventory;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import org.ltae.ui.BaseEcsUI;

/**
 * 库存UI
 */
public class ItemSlotGrid extends BaseEcsUI {

    // 拖拽功能
    private DragAndDrop dragAndDrop;
    // 拖拽来源黑名单
    private Array<Actor> dragBlacklist;
    // 是否能拖到地上
    private boolean canDragStop;
    // 归属者(实体id)
    public int ownerId;
    public Entity owner;

    private Array<Array<SlotDatum>> slotData;
    // 格子尺寸
    private int slotSize;
    // ui
    public Table slotTable;
    public ItemSlot.ItemSlotStyle itemSlotStyle;
    public ItemSlot[][] slots;


    public ItemSlotGrid(World world, DragAndDrop dragAndDrop, String slotStyleName) {
        super(world);
        this.dragAndDrop = dragAndDrop;

        itemSlotStyle = skin.get(slotStyleName, ItemSlot.ItemSlotStyle.class);
        dragBlacklist = new Array<>();

        slotSize = 32;
        canDragStop = true;
        initUI();
    }
    public void initUI(){
        slotTable = new Table();
        add(slotTable);
    }

    // 是否能拖到地上
    public void setCanDragStop(boolean canDragStop) {
        this.canDragStop = canDragStop;
    }

    // 归属者
    public void setOwner(int ownerId) {
        this.ownerId = ownerId;
        this.owner = world.getEntity(ownerId);
    }
    public Entity getOwner() {
        return owner;
    }
    public int getOwnerId() {
        return ownerId;
    }
    // 格子尺寸
    public void setSlotSize(int slotSize) {
        this.slotSize = slotSize;
    }
    // 拖拽来源黑名单
    public void addDragBlack(Actor actor){
        dragBlacklist.add(actor);
    }

    public void rmDragBlack(Actor actor){
        dragBlacklist.removeValue(actor,true);
    }

    // 数据
    public void setSlotData(Array<Array<SlotDatum>> slotData){
        this.slotData = slotData;
    }
    public Array<Array<SlotDatum>> getSlotData() {
        return slotData;
    }


    // 将所有的格子以及数据画出来
    public void rebuild() {
        if (slotData == null) {
            Gdx.app.error(getTag(),"Failed to rebuild,'slotData' is null!Please run function with 'setSlotData()'");
            return;
        }

        slotTable.clear();

        int rows = slotData.size;
        int cols = slotData.get(0).size;
        slots = new ItemSlot[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                ItemSlot itemSlot = new ItemSlot(world, itemSlotStyle);
                slotTable.add(itemSlot).size(slotSize);
                itemSlot.setInvPos(r,c);
                itemSlot.setInvUI(this);
                if (owner != null){
                    itemSlot.setOwner(ownerId);
                }
                slots[r][c] = itemSlot;
                enableDrag(itemSlot);

                SlotDatum slotDatum = slotData.get(r).get(c);
                itemSlot.setSlotDatum(slotDatum);
            }
            slotTable.row();
        }
    }

    public ItemSlot getSlot(int x, int y){
        if (slots == null) {
            return null;
        }
        return slots[x][y];
    }

    /* ===== 拖拽能力 ===== */
    public void enableDrag(ItemSlot slot) {
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
                //黑名单中的库存无法触发拖放
                ItemSlot fromSlot = (ItemSlot)source.getActor();
                if (dragBlacklist.contains(fromSlot.getInvUI(),true)) {
                    return;
                }
                onDrop(source,payload,getActor());
            }

        });
    }

    /* ===== 子类唯一要关心的两个钩子 ===== */
    //按住开始拖拽
    public DragAndDrop.Payload onDragStart(ItemSlot slot) {
        DragAndDrop.Payload payload = new DragAndDrop.Payload();
        Image dragActor = new Image(slot.getIcon().getDrawable());
        dragActor.setScale(4f);
        payload.setDragActor(dragActor);
        return payload;
    }
    // 拖到空地
    public void onDragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target){

    }
    // 拖拽后松开按钮
    public void onDrop(DragAndDrop.Source source,
                          DragAndDrop.Payload payload,
                          Actor targetActor) {
        if (slots == null) {
            Gdx.app.debug(getTag(),"Failed to swap item,'slots' is null!");
            return;
        }

        ItemSlot fromSlot = (ItemSlot)source.getActor();
        ItemSlot targetSlot = (ItemSlot)targetActor;

        //不允许自己拖拽到自己
        if (fromSlot == targetSlot){
            return;
        }


        SlotDatum fromDatum = fromSlot.getSlotDatum();
        SlotDatum targetDatum = targetSlot.getSlotDatum();
        //同一种物品需要合并
        if (fromDatum.itemEquals(targetDatum)) {
            merge(fromSlot,targetSlot);
            return;
        }

        //不同物品需要交换
        exchangeData(fromSlot,targetSlot);
    }

    // 合并数据
    public void merge(ItemSlot fromSlot, ItemSlot targetSlot){
        SlotDatum fromDatum = fromSlot.getSlotDatum();
        SlotDatum targetDatum = targetSlot.getSlotDatum();
        if (targetDatum.stackAmount == targetDatum.maxStack) {
            exchangeData(fromSlot,targetSlot);
            return;
        }

        int canSetAmount = targetDatum.maxStack - targetDatum.stackAmount;
        if (fromDatum.stackAmount <= canSetAmount){
            targetDatum.stackAmount += fromDatum.stackAmount;
            fromDatum = new SlotDatum();
        }else {
            targetDatum.stackAmount += canSetAmount;
            fromDatum.stackAmount -= canSetAmount;
        }

        ItemSlotGrid fromInvUI = fromSlot.getInvUI();
        ItemSlotGrid targetInvUI = targetSlot.getInvUI();
        fromInvUI.slotData.get(fromSlot.getInvX()).set(fromSlot.getInvY(), fromDatum);
        targetInvUI.slotData.get(targetSlot.getInvX()).set(targetSlot.getInvY(), targetDatum);
        fromInvUI.rebuild();
        if (fromInvUI != targetInvUI) {
            targetInvUI.rebuild();
        }
    }

    // 交换数据
    public void exchangeData(ItemSlot fromSlot, ItemSlot targetSlot){
        SlotDatum fromDatum = fromSlot.getSlotDatum();
        SlotDatum targetDatum = targetSlot.getSlotDatum();

        fromDatum.exchange(targetDatum);

        ItemSlotGrid fromInvUI = fromSlot.getInvUI();
        ItemSlotGrid targetInvUI = targetSlot.getInvUI();

        fromInvUI.slotData.get(fromSlot.getInvX()).set(fromSlot.getInvY(), fromDatum);
        targetInvUI.slotData.get(targetSlot.getInvX()).set(targetSlot.getInvY(), targetDatum);

        fromInvUI.rebuild();
        if (fromInvUI != targetInvUI) {
            targetInvUI.rebuild();
        }

    }
}
