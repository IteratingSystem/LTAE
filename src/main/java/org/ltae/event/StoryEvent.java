package org.ltae.event;

import com.bladecoder.ink.runtime.Story;

/**
 * @Auther WenLong
 * @Date 2025/6/30 17:33
 * @Description 剧本事件
 **/
public class StoryEvent extends TypeEvent{
    public static final int GET_STORY = 1;
    public static final int GET_LINES = 2;
    public String name;
    public Story story;

    public StoryEvent(int type) {
        super(type);
    }
}
