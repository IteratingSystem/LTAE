package org.ltae.component;

import com.artemis.Component;
import org.ltae.enums.HorizontalDir;
import org.ltae.enums.OrthogonalDir;
import org.ltae.enums.VerticalDir;
import org.ltae.tiled.TileCompLoader;
import org.ltae.tiled.TileDetails;
import org.ltae.tiled.TileParam;

/**
 * @Auther WenLong
 * @Date 2025/3/20 16:50
 * @Description 方向组件, 可以直接从横向纵向或者平面方向三个变量中选择自己需要使用的变量
 **/
public class Direction extends Component implements TileCompLoader {
    public HorizontalDir horizontal;
    public OrthogonalDir orthogonal;
    public VerticalDir vertical;

    @TileParam
    public String horizontalDir;
    @TileParam
    public String orthogonalDir;
    @TileParam
    public String verticalDir;
    @Override
    public void loader(TileDetails tileDetails) {
        if (!"NULL".equals(horizontalDir)){
            horizontal = HorizontalDir.valueOf(horizontalDir);
        }
        if (!"NULL".equals(orthogonalDir)){
            orthogonal = OrthogonalDir.valueOf(orthogonalDir);
        }
        if (!"NULL".equals(verticalDir)){
            vertical = VerticalDir.valueOf(verticalDir);
        }
    }
}
