package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

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
    private ObjectMap<Class<? extends Table>,Table> uiMap;
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

    public void register(Table ui){
        if (uiMap.containsKey(ui.getClass())) {
            Gdx.app.log(TAG,"Failed to addUI,uiMap is containsKey: "+ ui.getClass().getSimpleName());
            return;
        }
        uiMap.put(ui.getClass(),ui);
        stack.addActor(ui);
    }
    public Table getUI(Class<? extends Table> zClass){
        if (!uiMap.containsKey(zClass)) {
            Gdx.app.log(TAG,"Failed to getUI,uiMap is not containsKey: "+zClass.getSimpleName());
            return null;
        }
        return uiMap.get(zClass);
    }
    public void hideUI(Class<? extends Table> zClass){
        if (!uiMap.containsKey(zClass)) {
            Gdx.app.log(TAG,"Failed to hideUI,uiMap is not containsKey: "+zClass.getSimpleName());
            return;
        }
        getUI(zClass).setVisible(false);
    }
    public void showUI(Class<? extends Table> zClass){
        if (!uiMap.containsKey(zClass)) {
            Gdx.app.log(TAG,"Failed to showUI,uiMap is not containsKey: "+zClass.getSimpleName());
            return;
        }
        getUI(zClass).setVisible(true);
    }
}
