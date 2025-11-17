package org.ltae.event;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:42
 * @Description
 **/
public class UIEvent extends TypeEvent{
    public static final int HIDE = 1;
    //显示ui,需要传入uiClass作为需要显示的目标ui
    public static final int SHOW = 2;
    public static final int REGISTER = 3;
    public static final int GET_TABLE = 4;
    public static final int HIDE_ALL = 5;
    public static final int ONLY_SHOW = 6;

    public Class<? extends Table> uiClass;
    public Table table;

    public UIEvent(int type) {
        super(type);
    }
}
