package org.ltae.component;


import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;

/**
 * 操控组件
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/23 11:52
 * @see CtrlComp
 */
public class CtrlComp extends SerializeComponent {
    // 是否开启操控
    @SerializeParam
    public boolean enabled;
}
