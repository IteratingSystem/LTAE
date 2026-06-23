package org.ltae.manager.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.Timer;

public class LongPressProcessor implements InputProcessor {
    private IntMap<Timer.Task> keyTasks = new IntMap<>(); // 每个按键对应的延迟任务
    private static final long LONG_PRESS_DURATION = 500; // 毫秒
    private SnapshotArray<LongPressListener> listeners = new SnapshotArray<>();

    public void addListener(LongPressListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LongPressListener listener) {
        listeners.removeValue(listener, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        // 如果该键已有任务，先取消（防止重复）
        cancelTask(keycode);

        // 创建延迟任务，到达阈值后触发长按
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                // 触发长按回调
                for (LongPressListener listener : listeners) {
                    listener.onLongPress(keycode);
                }
                // 任务执行后从Map中移除（表示长按已触发）
                keyTasks.remove(keycode);
            }
        };
        // 调度任务，延迟 LONG_PRESS_DURATION 毫秒后执行
        Timer.schedule(task, LONG_PRESS_DURATION / 1000f);
        keyTasks.put(keycode, task);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Timer.Task task = keyTasks.remove(keycode);
        if (task != null) {
            // 任务还存在，说明长按尚未触发，取消任务并视为短按
            task.cancel();
            for (LongPressListener listener : listeners) {
                listener.onShortPress(keycode);
            }
        }
        // 如果 task == null，说明长按已经触发过了，此时抬起不做额外处理（或可触发“释放”事件，视需求而定）
        return true;
    }

    // 取消某个按键的任务
    private void cancelTask(int keycode) {
        Timer.Task task = keyTasks.remove(keycode);
        if (task != null) {
            task.cancel();
        }
    }

    // 其他方法空实现（触摸等）
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}