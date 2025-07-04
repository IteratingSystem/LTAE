package org.ltae.component;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import org.ltae.tiled.TileParam;
import org.ltae.utils.serialize.Serialize;

/**
 * @Auther WenLong
 * @Date 2025/5/15 10:35
 * @Description 闹钟,用于定时
 **/
public class AlarmClock extends Component {
    //定时,单位毫秒
    @Serialize
    @TileParam
    public float atTime;
    @Serialize
    public boolean isFinished = false;
}
