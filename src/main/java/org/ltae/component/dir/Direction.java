package org.ltae.component.dir;

import com.artemis.World;
import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;
import org.ltae.serialize.data.EntityDatum;

/**
 * @Auther WenLong
 * @Date 2025/3/20 16:50
 * @Description 方向组件, 可以直接从横向纵向或者平面方向三个变量中选择自己需要使用的变量
 **/
public class Direction extends SerializeComponent {
    @SerializeParam
    public HorizontalDir horizontalDir;
    @SerializeParam
    public OrthogonalDir orthogonalDir;
    @SerializeParam
    public VerticalDir verticalDir;
    @Override
    public void reload(World world, EntityDatum entityDatum) {
        super.reload(world, entityDatum);
    }
}
