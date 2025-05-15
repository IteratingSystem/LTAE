package org.ltae.component;

import com.artemis.Component;
import org.ltae.tiled.TileParam;

/**
 * @Auther WenLong
 * @Date 2025/5/15 10:35
 * @Description 闹钟,用于定时
 **/
public class AlarmClock extends Component {
    //定时,单位毫秒
    @TileParam
    public float atTime;
    public boolean isFinished = false;
}
