package org.worldloom.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.worldloom.component.PointLight;
import org.worldloom.component.Pos;

/**
 * 更新Box2D Lights传统点光源。
 */
@All({Pos.class, PointLight.class})
public class PointLightSystem extends IteratingSystem {
    private LightSystem lightSystem;
    private M<Pos> mPos;
    private M<PointLight> mPointLight;

    @Override
    protected void process(int entityId) {
        if (lightSystem.rayHandler == null) {
            return;
        }

        PointLight pointLight = mPointLight.get(entityId);
        if (pointLight.light == null) {
            pointLight.light = new box2dLight.PointLight(
                lightSystem.rayHandler,
                pointLight.rays,
                pointLight.color,
                pointLight.distance,
                pointLight.offsetX,
                pointLight.offsetY);
        }

        Pos pos = mPos.get(entityId);
        pointLight.light.setActive(pointLight.onOff);
        pointLight.light.setPosition(
            pos.x + pointLight.offsetX,
            pos.y + pointLight.offsetY);
    }
}
