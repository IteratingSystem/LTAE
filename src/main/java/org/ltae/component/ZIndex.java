package org.ltae.component;

import com.artemis.Component;
import org.ltae.tiled.TileParam;


/**
 * @Auther WenLong
 * @Date 2025/2/12 17:11
 * @Description 渲染组件
 **/
public class ZIndex extends Component {
    @TileParam
    public float index = 0;
    @TileParam
    public float offset = 0;
}
