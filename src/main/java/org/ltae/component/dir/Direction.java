package org.ltae.component.dir;

import com.artemis.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
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
    public void write(Json json) {
        super.write(json);
        json.writeValue("horizontalDir", horizontalDir != null ? horizontalDir.name() : null);
        json.writeValue("orthogonalDir", orthogonalDir != null ? orthogonalDir.name() : null);
        json.writeValue("verticalDir", verticalDir != null ? verticalDir.name() : null);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        String hDir = jsonData.has("horizontalDir") ? jsonData.getString("horizontalDir") : null;
        horizontalDir = hDir != null ? HorizontalDir.valueOf(hDir) : null;
        String oDir = jsonData.has("orthogonalDir") ? jsonData.getString("orthogonalDir") : null;
        orthogonalDir = oDir != null ? OrthogonalDir.valueOf(oDir) : null;
        String vDir = jsonData.has("verticalDir") ? jsonData.getString("verticalDir") : null;
        verticalDir = vDir != null ? VerticalDir.valueOf(vDir) : null;
    }
}
