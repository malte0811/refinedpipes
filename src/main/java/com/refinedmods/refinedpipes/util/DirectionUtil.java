package com.refinedmods.refinedpipes.util;

import net.minecraft.core.Direction;

public class DirectionUtil {
    public static Direction safeGet(byte d) {
        Direction[] v = Direction.values();

        if (d < 0 || d >= v.length) {
            return Direction.NORTH;
        }

        return v[d];
    }
}
