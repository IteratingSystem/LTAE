package org.ltae.component;

import com.artemis.Component;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class ZIndex extends SerializeComponent {
    @SerializeParam
    public float index;
    @SerializeParam
    // offset越大,月靠上边,则越被遮挡
    public float offset;
    @SerializeParam
    public boolean followY;
}
