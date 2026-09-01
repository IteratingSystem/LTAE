package org.worldloom.component;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.serialize.SerializeParam;
import org.worldloom.serialize.data.EntityDatum;

/**
 * Box2D Lights传统点光源组件。
 */
public class PointLight extends SerializeComponent implements Disposable {
    @SerializeParam
    public float offsetX;
    @SerializeParam
    public float offsetY;
    @SerializeParam
    public float distance;
    @SerializeParam
    public Color color;
    @SerializeParam
    public int rays;
    @SerializeParam
    public boolean onOff;

    public box2dLight.PointLight light;

    @Override
    public void reload(World world, EntityDatum entityDatum) {
        dispose();
        super.reload(world, entityDatum);
    }

    @Override
    public void dispose() {
        if (light == null) {
            return;
        }
        light.remove();
        light = null;
    }
}
