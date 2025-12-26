package org.ltae.ui;

import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.mostlyoriginal.api.event.common.EventSystem;
import org.ltae.system.AssetSystem;


/**
 * @Auther WenLong
 * @Date 2024/11/26 14:18
 * @Description
 **/
public class BaseEcsUI extends Table {
    public World world;
    public AssetSystem assetSystem;
    public TagManager tagManager;
    public EventSystem eventSystem;
    public Skin skin;
    public BaseEcsUI(World world){
        this.world = world;
        tagManager = world.getSystem(TagManager.class);
        assetSystem = world.getSystem(AssetSystem.class);
        eventSystem = world.getSystem(EventSystem.class);
        skin = assetSystem.skin;
    }
    public String getTag(){
        return getClass().getSimpleName();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
