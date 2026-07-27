package frc.tecdroid3354.utils

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.units.measure.Distance

/**
 * Creates a virtual box, which is intended to use as a boundary.
 *
 * Use standard Blue-Alliance coordinates (measures will match the ones in field drawings).
 *
 * Think of [minX], [minY] as the bottom-left corner of your boundary,
 * and [maxX], [maxY] as the top-right corner.
 */
class Boundary(private val minX: Distance, private val minY: Distance,
               private val maxX: Distance, private val maxY: Distance) {
    /**
     * Checks if the given [Translation2d] is within boundaries.
     */
    fun contains(translation: Translation2d): Boolean {
        return translation.measureX.gte(minX)
                && translation.measureY.gte(minY)
                && translation.measureX.lte(maxX)
                && translation.measureY.lte(maxY)
    }

    /**
     * Returns a [List] containing the four corners of the boundary.
     *
     * This is intended to use with standard Blue-Alliance coordinates and returns the list in
     * counter-clockwise order of coordinates.
     *
     * Therefore, if you were to publish the boundary to visualize it in AdvantageScope, you would create the sides
     * as follows:
     *
     * list[[0]] -> list[[1]],
     *
     * list[[1]] -> list[[2]],
     *
     * list[[2]] -> list[[3]],
     *
     * list[[3]] -> list[[0]]
     */
    fun getBoundaryCorners(): List<Translation2d> {
        return listOf(Translation2d(minX, minY), Translation2d(maxX, minY),
            Translation2d(maxX, maxY), Translation2d(minX, maxY))
    }
}