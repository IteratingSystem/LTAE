package org.ltae.manager;

import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.bladecoder.ink.runtime.Story;

/**
 * @Auther WenLong
 * @Date 2025/7/1 9:56
 * @Description 剧本管理器
 **/
public class StoryManager {
    private static final String TAG = StoryManager.class.getSimpleName();
    private static final String EXT = ".ink.json";

    private static Story story;
    private static ObjectMap<String, Story> storyData;

    // 获取当前剧本
    public static Story getCurrent(){
        return story;
    }
    // 通过名字获取剧本
    public static Story getStory(String name){
        if (storyData == null){
            storyData = AssetManager.getInstance().getObjects(EXT,Story.class);
        }
        if (!storyData.containsKey(name)) {
            Gdx.app.error(TAG, "Story not found: " + name);
            return null;
        }
        return storyData.get(name);
    }
    // 改变当前剧本
    public static Story changeStory(String name){
        story = getStory(name);
        return story;
    }

    public static Bag<String> getLines(){
        Bag<String> sentences = new Bag<>();
        while (story.canContinue()) {
            String line = "";
            try {
                line = story.Continue();
            } catch (Exception e) {
                Gdx.app.error(TAG,"Failed to getSentences:RuntimeException;");
                throw new RuntimeException(e);
            }
            sentences.add(line);
        }
        return sentences;
    }
    private static void resetState(){
        try {
            story.resetState();
        } catch (Exception e) {
            Gdx.app.error(TAG,"Failed to reset state");
            throw new RuntimeException(e);
        }
    }
    public static void Continue(){
        try {
            story.Continue();
        } catch (Exception e) {
            Gdx.app.error(TAG,"Failed to reset state");
            throw new RuntimeException(e);
        }
    }
    public static boolean isFinished(){
        return !story.canContinue() && story.getCurrentChoices().isEmpty();
    }
}
