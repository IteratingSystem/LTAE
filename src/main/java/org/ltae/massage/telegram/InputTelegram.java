package org.ltae.massage.telegram;


import com.badlogic.gdx.ai.msg.Telegram;

/**
 * 输入处理电报
 *
 * <p></p>
 *
 * @author WenLong
 * @version 1.0.0
 * @date 2026/6/23 15:46
 * @see InputTelegram
 */
public class InputTelegram extends Telegram {
    public final static int PRESS = 0;
    public final static int LONG_PRESS = 1;
    public final static int SHOUT_PRESS = 2;
    public final static int JUST_PRESS = 3;

    public int pressType;
    public int keycode;

    public InputTelegram(int pressType, int keycode) {
        super();
        this.pressType = pressType;
        this.keycode = keycode;
    }
}
