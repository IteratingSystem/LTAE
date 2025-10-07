package org.ltae.component;

import com.artemis.Component;
import org.ltae.manager.map.serialize.SerializeParam;

/**
 * 图层采样组件
 */
public class LayerSampling extends Component {
    @SerializeParam
    public String layerName;
    @SerializeParam
    public boolean update;
}
