package org.ltae.event;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @Auther WenLong
 * @Date 2025/6/30 14:42
 * @Description
 **/
public class UIEvent extends TypeEvent{
    public static final int HIDE = 1;
    public static final int SHOW = 2;
    public static final int REGISTER = 3;
    public static final int GET_TABLE = 4;

    public Class<? extends Table> uiClass;
    public Table table;
}
