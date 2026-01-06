package org.ltae.component;

import com.artemis.Component;
import org.ltae.serialize.SerializeParam;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class ZIndex extends Component {
    @SerializeParam
    public float index = 0;
    @SerializeParam
    public float offset = 0;
    @SerializeParam
    public boolean followY = false;
}
