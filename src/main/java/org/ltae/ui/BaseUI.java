package org.ltae.ui;

import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.ltae.system.AssetSystem;


/**
 * @Auther WenLong
 * @Date 2024/11/26 14:18
 * @Description
 **/
public class BaseUI extends Table {
    public World world;
    public AssetSystem assetSystem;
    public TagManager tagManager;
    public Skin skin;
    public BaseUI(World world){
        this.world = world;
        tagManager = world.getSystem(TagManager.class);
        assetSystem = world.getSystem(AssetSystem.class);
        skin = assetSystem.skin;
    }
    public String getTag(){
        return getClass().getSimpleName();
    }
}
