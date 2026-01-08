package org.ltae.state;

import com.artemis.Entity;
import com.badlogic.gdx.ai.fsm.State;

public interface EceState extends State<Entity> {
    void initialize(Entity entity);
}
