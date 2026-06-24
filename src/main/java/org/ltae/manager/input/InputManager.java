package org.ltae.manager.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * 全局输入管理器
 * <ul>
 *     <li>自动初始化 InputMultiplexer 并设置为全局处理器</li>
 *     <li>提供长按/短按监听器的注册与移除</li>
 *     <li>支持动态添加/移除其他 InputProcessor</li>
 * </ul>
 */
public class InputManager {
    private static InputMultiplexer MULTIPLEXER;
    private static LongPressProcessor LONG_PRESS_PROCESSOR;

    // ---------- 初始化 ----------
    private static InputMultiplexer getMultiplexer() {
        if (MULTIPLEXER == null) {
            MULTIPLEXER = new InputMultiplexer();
            Gdx.input.setInputProcessor(MULTIPLEXER);
        }
        return MULTIPLEXER;

    }
    private static LongPressProcessor getLongPressProcessor() {
        if (LONG_PRESS_PROCESSOR == null) {
            LONG_PRESS_PROCESSOR = new LongPressProcessor();
            getMultiplexer().addProcessor(LONG_PRESS_PROCESSOR);
        }
        return LONG_PRESS_PROCESSOR;
    }



    // ---------- 长按监听 API（钩子） ----------
    public static void addLongPressListener(LongPressListener listener) {
        getLongPressProcessor().addListener(listener);
    }
    public static void removeLongPressListener(LongPressListener listener) {
        getLongPressProcessor().removeListener(listener);
    }

    // ---------- 通用处理器管理 ----------
    public static void addProcessor(InputProcessor processor) {

        getMultiplexer().addProcessor(processor);
    }

    public static void addProcessor(int index, InputProcessor processor) {
        getMultiplexer().addProcessor(index, processor);
    }

    public static void removeProcessor(InputProcessor processor) {
        getMultiplexer().removeProcessor(processor);
    }

    public static void removeProcessor(int index) {
        getMultiplexer().removeProcessor(index);
    }

    public static SnapshotArray<InputProcessor> getProcessors() {
        return getMultiplexer().getProcessors();
    }

    public static void clear() {
        getMultiplexer().clear();
    }


    public static boolean isKeyPressed(int key){
        return Gdx.input.isKeyPressed(key);
    }
    public static boolean isKeyPressed(int[] keys){
        for (int key : keys) {
            if (!Gdx.input.isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
    public static boolean isKeyJustPressed(int key){
        return Gdx.input.isKeyJustPressed(key);
    }
    public static boolean isKeyJustPressed(int[] keys){
        for (int key : keys) {
            if (!Gdx.input.isKeyJustPressed(key)) {
                return false;
            }
        }
        return true;
    }
}