package org.worldloom.component;

import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.serialize.SerializeParam;

/**
 * 保存当前使用者的实体ID。
 */
public class User extends SerializeComponent {
    @SerializeParam
    public int id = -1;
}
