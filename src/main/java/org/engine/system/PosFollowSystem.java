package org.engine.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.engine.component.B2dBody;
import org.engine.component.Pos;


/**
 * @Auther WenLong
 * @Date 2025/2/18 10:10
 * @Description 坐标跟随b2dBody
 **/
@All({Pos.class, B2dBody.class})
public class PosFollowSystem extends IteratingSystem {
    private M<Pos> mPos;
    private M<B2dBody> mB2dBody;

    private float worldScale;
    public PosFollowSystem(float worldScale){
        this.worldScale = worldScale;
    }
    @Override
    protected void process(int entityId) {
        B2dBody b2dBody = mB2dBody.get(entityId);
        Vector2 position = b2dBody.body.getPosition();

        Pos pos = mPos.get(entityId);
        pos.x = position.x/worldScale;
        pos.y = position.y/worldScale;
    }
}
