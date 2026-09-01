package org.worldloom.component;

import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.serialize.SerializeParam;

/**
 * 保存实体归属者的实体ID。
 */
public class Owner extends SerializeComponent {
    @SerializeParam
    public int id = -1;
}
