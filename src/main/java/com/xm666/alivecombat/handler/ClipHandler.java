package com.xm666.alivecombat.handler;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class ClipHandler {
    public static Optional<Vec3> expandedClip(AABB aabb, Vec3 from, Vec3 to) {
        var maxDistance = new double[1];
        var deltaX = to.x - from.x;
        var deltaY = to.y - from.y;
        var deltaZ = to.z - from.z;
        var direction = getDirection(aabb, from, maxDistance, null, deltaX, deltaY, deltaZ);
        if (direction == null) {
            return Optional.empty();
        } else {
            var maxDistanceValue = maxDistance[0];
            return Optional.of(from.add(maxDistanceValue * deltaX, maxDistanceValue * deltaY, maxDistanceValue * deltaZ));
        }
    }

    @Nullable
    private static Direction getDirection(AABB aabb, Vec3 start, double[] maxDistance, @Nullable Direction facing, double deltaX, double deltaY, double deltaZ) {
        if (deltaX > 1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaX, aabb.minX, Direction.WEST, start.x);
        } else if (deltaX < -1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaX, aabb.maxX, Direction.EAST, start.x);
        }

        if (deltaY > 1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaY, aabb.minY, Direction.DOWN, start.y);
        } else if (deltaY < -1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaY, aabb.maxY, Direction.UP, start.y);
        }

        if (deltaZ > 1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaZ, aabb.minZ, Direction.NORTH, start.z);
        } else if (deltaZ < -1.0E-7) {
            facing = clipPoint(maxDistance, facing, deltaZ, aabb.maxZ, Direction.SOUTH, start.z);
        }

        return facing;
    }

    @Nullable
    private static Direction clipPoint(double[] maxDistance, @Nullable Direction prevDirection, double distanceSide, double minSide, Direction hitSide, double startSide) {
        var distance = (minSide - startSide) / distanceSide;
        if (0.0 < distance && distance >= maxDistance[0]) {
            maxDistance[0] = distance;
            return hitSide;
        } else {
            return prevDirection;
        }
    }
}
