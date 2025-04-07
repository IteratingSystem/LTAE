package org.ltae.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import org.ltae.box2d.KeyframeShapeData;
import org.ltae.box2d.setup.FixtureSetup;
import org.ltae.component.B2dBody;
import org.ltae.component.TileAnimations;

/**
 * @Auther WenLong
 * @Date 2025/4/7 14:22
 * @Description 动画帧中响应的形状
 **/
@All({B2dBody.class, TileAnimations.class})
public class KeyframeShapeSystem extends IteratingSystem {
    private M<B2dBody> mB2dBody;
    private M<TileAnimations> mTileAnimations;
    @Override
    protected void process(int entityId) {
        B2dBody b2dBody = mB2dBody.get(entityId);
        Body body = b2dBody.body;
        Bag<FixtureSetup> keyframeFixSetups = b2dBody.keyframeFixSetups;

        TileAnimations tileAnimations = mTileAnimations.get(entityId);
        String aniName = tileAnimations.current;
        int keyframeIndex = tileAnimations.getTileAnimation().getKeyframeIndex();

        Array<Fixture> fixtureList = body.getFixtureList();
        Array.ArrayIterator<Fixture> iterator = fixtureList.iterator();
        //删除所有帧形状
        while (iterator.hasNext()) {
            Fixture fixture = iterator.next();
            Object userData = fixture.getUserData();
            if (userData instanceof KeyframeShapeData keyframeShapeData) {
                body.destroyFixture(fixture);
            }
        }

        //创建响应的帧形状
        for (FixtureSetup keyframeFixSetup : keyframeFixSetups) {
            KeyframeShapeData fixtureData = (KeyframeShapeData)keyframeFixSetup.fixtureData;
            if (aniName.equals(fixtureData.aniName) && keyframeIndex == fixtureData.keyframeIndex) {
                FixtureDef keyframeFixDef = b2dBody.getKeyframeFixDef(fixtureData);
                Fixture fixture = body.createFixture(keyframeFixDef);
                fixture.setUserData(fixtureData);
            }
        }
    }
}
