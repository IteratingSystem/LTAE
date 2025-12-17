package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.component.B2dBody;
import org.ltae.component.Inert;
import org.ltae.component.Pos;


/**
 * @Auther WenLong
 * @Date 2025/2/18 10:10
 * @Description 坐标跟随b2dBody
 **/
@All({Pos.class, B2dBody.class})
@Exclude(Inert.class)
public class PosFollowBodySystem extends IteratingSystem {
    private M<Pos> mPos;
    private M<B2dBody> mB2dBody;

    private float worldScale;
    public PosFollowBodySystem(float worldScale){
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
