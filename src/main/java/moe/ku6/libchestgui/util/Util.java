package moe.ku6.libchestgui.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
    public static int ToInventorySlot(IntPair coord) {
        return coord.x() + coord.y() * 9;
    }

    public static IntPair FromInventorySlot(int slot) {
        return new IntPair(slot % 9, slot / 9);
    }
}
