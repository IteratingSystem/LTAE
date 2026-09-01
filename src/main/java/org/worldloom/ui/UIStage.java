package org.worldloom.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.worldloom.Worldloom;

/**
 * @Auther WenLong
 * @Date 2025/12/16 15:41
 * @Description 以UI为目的的Stage,控制其大小
 **/
public class UIStage extends Stage {
    public UIStage(){
        super(new FitViewport(
            Worldloom.config().getUiWidth() / Worldloom.config().getUiZoom(),
            Worldloom.config().getUiHeight() / Worldloom.config().getUiZoom()));
    }
    public UIStage(float privateZoom){
        super(new FitViewport(
            Worldloom.config().getUiWidth() / Worldloom.config().getUiZoom() / privateZoom,
            Worldloom.config().getUiHeight() / Worldloom.config().getUiZoom() / privateZoom));
    }
}
