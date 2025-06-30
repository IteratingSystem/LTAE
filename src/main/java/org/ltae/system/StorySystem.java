package org.ltae.system;

import com.artemis.BaseSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.bladecoder.ink.runtime.Story;
import net.mostlyoriginal.api.event.common.Subscribe;
import org.ltae.event.StoryEvent;

/**
 * @Auther WenLong
 * @Date 2025/6/30 17:10
 * @Description 剧本系统
 **/
public class StorySystem extends BaseSystem {
    private static final String TAG = StorySystem.class.getSimpleName();
    private AssetSystem assetSystem;
    private ObjectMap<String, Story> storyData;

    @Override
    protected void initialize() {
        super.initialize();
        storyData=assetSystem.storyData;
    }

    @Override
    protected void processSystem() {
    }
    private Story getStory(String name){
        return storyData.get(name);
    }
    private Bag<String> getLines(Story story){
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

    @Subscribe
    public Story onGetStoryEvent(StoryEvent event){
        if (event.type != StoryEvent.GET_STORY) {
            return null;
        }
        return getStory(event.name);
    }
    @Subscribe
    public Bag<String> noGetLineEvent(StoryEvent event){
        if (event.type != StoryEvent.GET_LINES) {
            return null;
        }
        return getLines(event.story);
    }
}
