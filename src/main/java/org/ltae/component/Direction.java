package org.ltae.component;

import com.artemis.World;
import org.ltae.enums.HorizontalDir;
import org.ltae.enums.OrthogonalDir;
import org.ltae.enums.VerticalDir;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityJson;

/**
 * @Auther WenLong
 * @Date 2025/3/20 16:50
 * @Description 方向组件, 可以直接从横向纵向或者平面方向三个变量中选择自己需要使用的变量
 **/
public class Direction extends SerializeComponent{
    public  HorizontalDir horizontal;
    public  OrthogonalDir orthogonal;
    public  VerticalDir vertical;

    @SerializeParam
    public String horizontalDir;
    @SerializeParam
    public String orthogonalDir;
    @SerializeParam
    public String verticalDir;
    @Override
    public void reload(World world, EntityJson entityJson) {
        super.reload(world,entityJson);
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
