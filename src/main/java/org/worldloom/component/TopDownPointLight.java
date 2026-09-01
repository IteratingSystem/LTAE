package org.worldloom.component;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import org.worldloom.component.parent.SerializeComponent;
import org.worldloom.serialize.SerializeParam;
import org.worldloom.serialize.data.EntityDatum;

/**
 * 俯视角点光源组件。
 */
public class TopDownPointLight extends SerializeComponent implements Disposable {
    @SerializeParam
    public float offsetX;
    @SerializeParam
    public float offsetY;
    @SerializeParam
    public float height = 64f;
    @SerializeParam
    public float distance = 128f;
    @SerializeParam
    public Color color = new Color(1f, 0.75f, 0.45f, 1f);
    @SerializeParam
    public int rays = 64;
    @SerializeParam
    public boolean onOff = true;

    public org.worldloom.light.TopDownPointLight light;

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
