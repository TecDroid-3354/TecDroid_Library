package frc.tecdroid3354.subsystems.linearDisplacement

import edu.wpi.first.units.measure.Distance
import frc.tecdroid3354.constants.RobotDimensions
import frc.tecdroid3354.constants.SubsystemsMovementLimits
import frc.tecdroid3354.utils.degrees
import frc.tecdroid3354.utils.meters
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.mechanism.LoggedMechanism2d
import org.littletonrobotics.junction.mechanism.LoggedMechanismLigament2d
import org.littletonrobotics.junction.mechanism.LoggedMechanismRoot2d

class ElevatorVisualization {
    //
    // VISUALIZATION IN 2D ONLY
    //
    private val elevator2d: LoggedMechanism2d = LoggedMechanism2d(
        ElevatorConstants.VisualizerConstants.CANVAS_WIDTH.meters,
        ElevatorConstants.VisualizerConstants.CANVAS_HEIGHT.meters,
        ElevatorConstants.VisualizerConstants.CANVAS_COLOR,
    )
    private val elevatorOrigin: LoggedMechanismRoot2d = elevator2d.getRoot(
        ElevatorConstants.VisualizerConstants.MECHANISM_ORIGIN_NAME,
        ElevatorConstants.VisualizerConstants.CANVAS_WIDTH.div(2.0).meters,
        0.0, // Starts at the bottom of the canvas
    )
    private val guidingRail: LoggedMechanismLigament2d = elevatorOrigin.append(
        LoggedMechanismLigament2d(
            ElevatorConstants.VisualizerConstants.MECHANISM_GUIDING_RAIL_NAME,
            RobotDimensions.ELEVATOR_MINIMUM_LENGTH.meters, // Initial length
            ElevatorConstants.VisualizerConstants.GUIDING_RAIL_INITIAL_ANGLE.degrees, // Can be updated
            ElevatorConstants.VisualizerConstants.GUIDING_RAIL_WIDTH,
            ElevatorConstants.VisualizerConstants.GUIDING_RAIL_COLOR
        )
    )
    private val hiddenDisplacementLigament: LoggedMechanismLigament2d = guidingRail.append(
        LoggedMechanismLigament2d(
            ElevatorConstants.VisualizerConstants.MECHANISM_DISPLACEMENT_LIGAMENT_NAME,
            (SubsystemsMovementLimits.ELEVATOR_DISPLACEMENT_LIMITS.minimum as Distance).meters, // Initial length
            ElevatorConstants.VisualizerConstants.DISPLACEMENT_LIGAMENT_INITIAL_ANGLE.degrees, // Relative to guiding rail
            ElevatorConstants.VisualizerConstants.DISPLACEMENT_LIGAMENT_WIDTH,
            ElevatorConstants.VisualizerConstants.DISPLACEMENT_LIGAMENT_COLOR
        )
    )
    private val carriageLigament: LoggedMechanismLigament2d = hiddenDisplacementLigament.append(
        LoggedMechanismLigament2d(
            ElevatorConstants.VisualizerConstants.MECHANISM_CARRIAGE_NAME,
            ElevatorConstants.VisualizerConstants.CARRIAGE_HEIGHT.meters,
            ElevatorConstants.VisualizerConstants.CARRIAGE_RELATIVE_ANGLE.degrees, // Relative to displacement ligament
            ElevatorConstants.VisualizerConstants.CARRIAGE_WIDTH,
            ElevatorConstants.VisualizerConstants.CARRIAGE_COLOR
        )
    )

    //
    // VISUALIZATION IN 3D ONLY (requires 3D CAD asset configured in AdvantageScope)
    //

    // TODO() Implement 3D Visualization

    /**
     * Takes the current displacement of the motor (through the IO class inside the subsystem layer) and uses
     * it to update the 2d and 3d visualization of the mechanism.
     *
     * **NOTE:** In case this subsystem is attached to a joint, its angle must be supplied to update the visualization.
     * This is not accounted for in this method right now.
     */
    fun updateElevatorVisualization(elevatorDisplacement: Distance) {
        hiddenDisplacementLigament.setLength(elevatorDisplacement)
        Logger.recordOutput(ElevatorConstants.Telemetry.SUBSYSTEM_VISUALIZATION_2D_TAB, elevator2d)
    }
}