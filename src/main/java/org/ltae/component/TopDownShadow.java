package org.ltae.component;

import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;

/**
 * 标记实体参与俯视角阴影：既产生阴影，也接收其他实体的阴影。
 */
public class TopDownShadow extends SerializeComponent {
    /** 大于0时使用指定纵深，否则自动取纹理世界宽度的五分之一。 */
    @SerializeParam
    public float depth;
}
