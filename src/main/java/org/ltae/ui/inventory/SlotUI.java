package org.ltae.ui.inventory;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/**
 * 库存单元格,默认拥有图片和堆叠数量,需要增加其它的数据或者样式直接继承就可以了
 */
public class SlotUI extends WidgetGroup {
    //相对于库存的格子位置,在一个库存页面中,左上角为原点0,0
    private Vector2 posForInventory;

    private World world;
    private SlotDatum slotDatum;

    private SlotStyle style;
    private Image bg;      // 背景
    private Image icon;    // 物品图标
    private Label amount;  // 右下角数量
    private boolean hovered, pressed, checked, disabled;
    //交换后记录fromInventory,也就是上一个父容器
    private InventoryUI oldInventory;

    //归属者(实体id)
    private int ownerId;
    private Entity owner;

    public SlotUI(World world, Skin skin, String styleName) {
        this(world,skin.get(styleName, SlotStyle.class));
    }

    public SlotUI(World world, SlotStyle style) {
        this.world = world;
        this.style = style;

        posForInventory = new Vector2(-1,-1);
        bg = new Image();
        icon = new Image();
        amount = new Label("", new Label.LabelStyle(style.font, style.fontColor));
        amount.setAlignment(Align.bottomRight);

        addActor(bg);
        addActor(icon);
        addActor(amount);

        // 统一大小
        setSize(style.up.getMinWidth(), style.up.getMinHeight());
        //首次绘制
        refreshDrawables();
        // 按钮事件（悬停/按下/点击）
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                hovered = true;
                refreshDrawables();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                hovered = false;
                refreshDrawables();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pressed = true;
                refreshDrawables();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pressed = false;
                refreshDrawables();
            }
        });
    }



    /* =============== 外部调用 =============== */
    //归属者
    public void setOwner(int ownerId) {
        this.ownerId = ownerId;
        this.owner = world.getEntity(ownerId);
    }
    public Entity getOwner() {
        return owner;
    }

    public Vector2 getPosForInventory() {
        return posForInventory;
    }

    public void setPosForInventory(int x,int y) {
        this.posForInventory.set(x,y);
    }

    public void setFromInventory(InventoryUI fromInventory) {
        this.oldInventory = fromInventory;
    }

    public InventoryUI getFromInventory() {
        return oldInventory;
    }


    /** 换背景图（普通/装备/任务…） */
    public void setBackground(Drawable drawable) {
        style.up = drawable;
        refreshDrawables();
    }

    public Image getIcon() {
        return icon;
    }

    /** 换物品图标 */
    private void setIcon(Drawable drawable) {
        icon.setDrawable(drawable);
        icon.setVisible(drawable != null);
    }

    public int getAmount() {
        String amountStr = amount.getText().toString();
        if (amountStr.isBlank()){
            amountStr = "0";
        }
        return Integer.parseInt(amountStr);
    }
    /** 数量文本 */
    private void setAmount(int count) {
        amount.setText(count <= 1 ? "" : String.valueOf(count));
    }

    public SlotDatum getSlotDatum() {
        return slotDatum;
    }

    public void setSlotDatum(SlotDatum slotDatum) {
        this.slotDatum = slotDatum;
        if (slotDatum == null){
            slotDatum = new SlotDatum();
        }
        setIcon(slotDatum.getDrawable(world));
        setAmount(slotDatum.stackAmount);
    }

    /** 选中状态 */
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawables();
    }

    public boolean isChecked() { return checked; }

    /** 禁用状态 */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        refreshDrawables();
    }

    public boolean isDisabled() { return disabled; }

    /* =============== 内部 =============== */

    public void refreshDrawables() {
        // 背景状态机
        Drawable front = style.up;
        if (disabled && style.disabled != null) front = style.disabled;
        else if (pressed && style.down != null) front = style.down;
        else if (hovered && style.over != null) front = style.over;
        else if (checked && style.checked != null) front = style.checked;
        bg.setDrawable(front);

        // 图标染色（可选）
        Color tint = disabled ? Color.GRAY : Color.WHITE;
        icon.setColor(tint);

        icon.setDrawable(icon.getDrawable());
    }


    @Override
    public void layout() {
        // 背景铺满
        bg.setBounds(0, 0, getWidth(), getHeight());
        // 图标居中
        float iconSize = Math.min(getWidth(), getHeight()) * 0.8f;
        icon.setBounds((getWidth() - iconSize) / 2, (getHeight() - iconSize) / 2, iconSize, iconSize);
        // 数量右下角
        amount.setBounds(getWidth() - amount.getPrefWidth() - 6, 2, amount.getPrefWidth(), amount.getPrefHeight());
    }


    public static class SlotStyle extends TextButton.TextButtonStyle {
    }
}
