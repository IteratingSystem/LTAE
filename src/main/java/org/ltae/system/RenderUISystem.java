package org.ltae.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.event.UIEvent;

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

    private void register(Table ui){
        if (uiMap.containsKey(ui.getClass())) {
            Gdx.app.log(TAG,"Failed to addUI,uiMap is containsKey: "+ ui.getClass().getSimpleName());
            return;
        }
        uiMap.put(ui.getClass(),ui);
        stack.addActor(ui);
    }
    private Table getTable(Class<? extends Table> zClass){
        if (!uiMap.containsKey(zClass)) {
            Gdx.app.log(TAG,"Failed to getUI,uiMap is not containsKey: "+zClass.getSimpleName());
            return null;
        }
        return uiMap.get(zClass);
    }

    private void hide(Class<? extends Table> zClass){
        getTable(zClass).setVisible(false);
    }
    private void hideAll(){
        ObjectMap.Keys<Class<? extends Table>> keys = uiMap.keys();
        for (Class<? extends Table> key : keys) {
            getTable(key).setVisible(false);
        }
    }
    private void show(Class<? extends Table> zClass){
        if (!uiMap.containsKey(zClass)) {
            Gdx.app.log(TAG,"Failed to showUI,uiMap is not containsKey: "+zClass.getSimpleName());
            return;
        }
        getTable(zClass).setVisible(true);
    }
    private void onlyShow(Class<? extends Table> zClass){
        hideAll();
        getTable(zClass).setVisible(true);
    }

    @Subscribe
    public void onEvent(UIEvent event){
        if (event.type == UIEvent.SHOW) {
            show(event.uiClass);
            return;
        }
        if (event.type == UIEvent.ONLY_SHOW) {
            onlyShow(event.uiClass);
            return;
        }
        if (event.type == UIEvent.HIDE) {
            hide(event.uiClass);
            return;
        }
        if (event.type == UIEvent.HIDE_ALL) {
            hideAll();
            return;
        }
        if (event.type == UIEvent.REGISTER) {
            register(event.table);
            return;
        }
        if (event.type == UIEvent.GET_TABLE){
            event.table = getTable(event.uiClass);
            return;
        }
    }
}
