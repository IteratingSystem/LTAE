package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.ltae.ui.BaseUI;

/**
 * @Auther WenLong
 * @Date 2024/11/26 9:55
 * @Description ui系统
 **/
public class RenderUISystem extends BaseSystem {
    private Stage mainStage;
    private Stack stack;
    private ExtendViewport extendViewport;
    public RenderUISystem(int width,int height){
        extendViewport = new ExtendViewport(width,height);
    }

    @Override
    protected void initialize() {
        stack = new Stack();
        stack.setFillParent(true);


        mainStage = new Stage(extendViewport);
        mainStage.addActor(stack);
        Gdx.input.setInputProcessor(mainStage);
    }



    @Override
    protected void processSystem() {
        mainStage.act();
        mainStage.draw();
    }

    public void addUI(BaseUI ui){
        stack.addActor(ui);
    }
}
