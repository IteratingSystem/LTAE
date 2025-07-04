package org.ltae.component;

import com.artemis.Component;
import org.ltae.enums.HorizontalDir;
import org.ltae.enums.OrthogonalDir;
import org.ltae.enums.VerticalDir;
import org.ltae.tiled.ComponentLoader;
import org.ltae.tiled.TileParam;
import org.ltae.tiled.details.EntityDetails;
import org.ltae.tiled.details.SystemDetails;
import org.ltae.utils.serialize.Serialize;

/**
 * @Auther WenLong
 * @Date 2025/3/20 16:50
 * @Description 方向组件, 可以直接从横向纵向或者平面方向三个变量中选择自己需要使用的变量
 **/
public class Direction extends SerializeComponent implements ComponentLoader {
    public  HorizontalDir horizontal;
    public  OrthogonalDir orthogonal;
    public  VerticalDir vertical;

    @Serialize
    @TileParam
    public String horizontalDir;
    @Serialize
    @TileParam
    public String orthogonalDir;
    @Serialize
    @TileParam
    public String verticalDir;
    @Override
    public void loader(SystemDetails systemDetails, EntityDetails entityDetails) {
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
