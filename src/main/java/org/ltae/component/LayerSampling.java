package org.ltae.component;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.ltae.manager.map.serialize.SerializeParam;
import org.ltae.manager.map.serialize.json.EntityData;


/**
 * 图层采样组件
 */
public class LayerSampling extends SerializeComponent {
    @SerializeParam
    public String layerName;
    //需要采样次数
    @SerializeParam
    public int needNum;
    //间隔时间
    @SerializeParam
    public float interval;

    //已采样次数
    public int crtNum;
    //已采样纹理
    public TextureRegion[] regions;

    @Override
    public void reload(World world, EntityData entityData) {
        super.reload(world, entityData);
        crtNum = 0;
        regions = new TextureRegion[needNum];
    }

    //是否已经采样完成
    public boolean isSampled(){
        for (int i = 0; i < needNum; i++) {
            if (regions[i] == null) {
                return false;
            }
        }
        return true;
    }
}
