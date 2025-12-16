package org.ltae.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.game.GameRule;
import org.ltae.LtaePluginRule;

/**
 * @Auther WenLong
 * @Date 2025/12/16 15:41
 * @Description 以UI为目的的Stage,控制其大小
 **/
public class UIStage extends Stage {
    public UIStage(){
        super(new FitViewport(LtaePluginRule.UI_WIDTH/LtaePluginRule.UI_ZOOM,LtaePluginRule.UI_HEIGHT/LtaePluginRule.UI_ZOOM));
    }
    public UIStage(float privateZoom){
        super(new FitViewport(LtaePluginRule.UI_WIDTH/LtaePluginRule.UI_ZOOM/privateZoom,LtaePluginRule.UI_HEIGHT/LtaePluginRule.UI_ZOOM/privateZoom));
    }
}
