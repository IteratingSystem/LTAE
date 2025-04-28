package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.ltae.ui.BaseUI;

/**
 * @Auther WenLong
 * @Date 2024/11/26 9:55
 * @Description ui系统
 **/
public class RenderUISystem extends BaseSystem {
    private final static String TAG = RenderUISystem.class.getSimpleName();
    private Stage mainStage;
    private Stack stack;
    private ExtendViewport extendViewport;
    private ObjectMap<String,BaseUI> uiMap;
    public RenderUISystem(int width,int height){
        extendViewport = new ExtendViewport(width,height);
    }

    @Override
    protected void initialize() {
        uiMap = new ObjectMap<>();

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

    public void addUI(String name,BaseUI ui){
        if (uiMap.containsKey(name)) {
            Gdx.app.log(TAG,"Failed to addUI,uiMap is containsKey: "+name);
            return;
        }
        uiMap.put(name,ui);
        stack.addActor(ui);
    }
    public BaseUI getUI(String name){
        if (!uiMap.containsKey(name)) {
            Gdx.app.log(TAG,"Failed to getUI,uiMap is not containsKey: "+name);
            return null;
        }
        return uiMap.get(name);
    }
    public void hideUI(String name){
        BaseUI ui = getUI(name);
        if (ui == null) {
            return;
        }
        getUI(name).setVisible(false);
    }
    public void showUI(String name){
        BaseUI ui = getUI(name);
        if (ui == null) {
            return;
        }
        getUI(name).setVisible(true);
    }
}
