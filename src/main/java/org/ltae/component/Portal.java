package org.ltae.component;

import org.ltae.component.parent.SerializeComponent;
import org.ltae.serialize.SerializeParam;
import org.ltae.system.MapTransitionSystem;

/** Tiled component describing a destination map and its arrival marker. */
public class Portal extends SerializeComponent {
    @SerializeParam
    public String targetMap;
    @SerializeParam
    public String targetPosEntity;

    /** Delegates the complete transition transaction to the engine system. */
    public void teleport(int[] entityIds, int playerEntityId, boolean switchMap) {
        world.getSystem(MapTransitionSystem.class)
            .teleport(this, entityIds, playerEntityId, switchMap);
    }
}
