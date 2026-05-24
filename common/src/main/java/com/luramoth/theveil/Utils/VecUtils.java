package com.luramoth.theveil.Utils;

import net.minecraft.world.phys.Vec3;

public class VecUtils {

    public static Vec3 flattenAndPreserveLength(Vec3 v, Axis axis) {
        double originalLength = v.length();
        if (originalLength == 0) return Vec3.ZERO;

        Vec3 flattened = Vec3.ZERO;
        switch (axis) {
            case X -> flattened = new Vec3(0, v.y, v.z);
            case Y -> flattened = new Vec3(v.x, 0, v.z);
            case Z -> flattened = new Vec3(v.x, v.y, 0);
        }

        if (flattened.lengthSqr() < 1.0E-4D)
            return Vec3.ZERO;

        return flattened.normalize().scale(originalLength);
    }
}
