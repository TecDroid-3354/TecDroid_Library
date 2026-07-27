package frc.tecdroid3354.constants

import frc.tecdroid3354.utils.Boundary
import frc.tecdroid3354.utils.meters

object FieldConstants {
    /**
     * Use it to store all field boundaries (think of them as box) that will have some effect
     * on robot functionality.
     *
     * For example, in 2026 REBUILT, if your driver was trying to shoot behind the Tower,
     * the robot should stop shooting to avoid FUELS colliding with tower bars.
     *
     * Use the [frc.tecdroid3354.utils.Boundary] class to specify the boundaries.
     */
    object BoundariesLimits {
        val TOWER_BOUNDARIES: Boundary = Boundary(
            0.0.meters, 0.0.meters, 0.0.meters, 0.0.meters
        )
    }
}