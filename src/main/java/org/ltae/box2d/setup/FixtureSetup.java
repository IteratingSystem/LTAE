package org.ltae.box2d.setup;

import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * @Auther WenLong
 * @Date 2025/4/7 11:43
 * @Description 预定的FixtureData和FixtureDef,用于在后面创建Fixture
 **/
public class FixtureSetup {
    public FixtureDef FixtureDef;
    public Object fixtureData;
    public FixtureSetup(FixtureDef fixtureDef,Object fixtureData){
        this.FixtureDef = fixtureDef;
        this.fixtureData = fixtureData;
    }
}
