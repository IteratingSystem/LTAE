package org.ltae.b2d;

import com.artemis.utils.Bag;

/**
 * @Auther WenLong
 * @Date 2025/3/27 10:22
 * @Description 碰撞过滤要用到的类别位
 **/
public enum CategoryBits {
    I((short) 0x0001),
    II((short) 0x0002),
    III((short) 0x0004),
    IV((short) 0x0008),
    V((short) 0x0010),
    VI((short) 0x0020),
    VII((short) 0x0040),
    VIII((short) 0x0080),
    IX((short) 0x0100),
    X((short) 0x0200),
    XI((short) 0x0400),
    XII((short) 0x0800),
    XIII((short) 0x1000),
    XIV((short) 0x2000),
    XV((short) 0x4000),
    XVI((short) 0x8000);

    private short bit;
    CategoryBits(short bit){
        this.bit = bit;
    }

    public short getBit() {
        return bit;
    }

    public String getName(){
        return this.name();
    }

    // 传入类位字符串得到掩码位,例如"BOUNDARY,PLAYER"
    public static short getMask(String names){
        Bag<Short> masks = new Bag<>();
        for (String maskName : names.split(",")) {
            masks.add(CategoryBits.valueOf(maskName).getBit());
        }
        short mask = 0;
        for (Short category : masks) {
            mask |= category; // 位或操作
        }
        return mask;
    }
}
