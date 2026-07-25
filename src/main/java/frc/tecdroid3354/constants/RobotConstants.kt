package frc.tecdroid3354.constants

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.MomentOfInertia
import edu.wpi.first.units.measure.Temperature
import edu.wpi.first.units.measure.Time
import edu.wpi.first.wpilibj.RobotBase
import frc.tecdroid3354.utils.degreesCelsius
import frc.tecdroid3354.utils.inches
import frc.tecdroid3354.utils.kilogramSquareMeters
import frc.tecdroid3354.utils.meters
import frc.tecdroid3354.utils.milliseconds

enum class RobotMode {
    REAL,
    SIM,
    REPLAY
}

object RobotConstants {
    val ROBOT_MODE                      : RobotMode = if (RobotBase.isReal()) RobotMode.REAL else RobotMode.SIM
    val LOOP_TIME                       : Time = 20.0.milliseconds
    const val TUNING_MODE               : Boolean = true
    const val MAIN_CONTROLLER_PORT      : Int = 0
}

/**
 * Contains all fixed dimensions of the robot that may be relevant. This includes measures relevant for simulation.
 */
object RobotDimensions {
    val BUMPERS_HEIGHT                  : Distance = 0.15.meters
    val BUMPERS_DEPTH                   : Distance = 0.10.meters

    val ROBOT_LENGTH                    : Distance = 26.5.inches.plus(BUMPERS_DEPTH)
    val ROBOT_WIDTH                     : Distance = 26.5.inches.plus(BUMPERS_DEPTH)
    val ROBOT_HEIGHT                    : Distance = 0.6.meters

    val INTAKE_LENGTH                   : Distance = 7.5.inches // Examples only, not included in template
    val INTAKE_WIDTH                    : Distance = 26.5.inches

    val JOINT_WIDTH                     : Distance = 6.5.inches

    val ELEVATOR_MINIMUM_LENGTH         : Distance = 0.8.meters     // Note that "length" differs from "displacement"
    val ELEVATOR_MAXIMUM_LENGTH         : Distance = 2.32.meters    // Minimum + Maximum displacement
}

/**
 * Contains all [edu.wpi.first.math.geometry.Translation2d] and [edu.wpi.first.math.geometry.Translation3d]
 * relevant to the robot. This includes simulation-relevant transformations.
 *
 * i.e., Robot's transformation from center to a subsystem.
 */
object RobotTransformations {
    val ROBOT_TO_SHOOTER = Translation3d(0.25.meters, (-0.15).meters, 0.5.meters)
    val ROBOT_TO_INTAKE = Translation2d(0.0.meters, 0.25.meters)
}

/**
 * Contains the name of each canbus present in the robot.
 * While every subsystem stores its own variable for this, said variable must be assigned to one of these values
 * to prevent any mismatch between IDs.
 */
object CanBuses {
    const val MAIN_CANBUS       : String = "rio"
    const val ALTERNATE_CANBUS  : String = "canivore"
}

object SimConstants {
    val NEUTRAL_MOTOR_TEMPERATURE: Temperature = 45.0.degreesCelsius

    // Based off Falcon 500, which should be fairly similar to Kraken x60 MOI
    val ESTIMATED_MOTOR_MOI         : MomentOfInertia = (3.6e-5).kilogramSquareMeters
    const val LB_SQUARED_IN_TO_KG_SQUARED_M: Double = 0.0002926397
}

/**
 * For robot-wise telemetry tabs.
 */
object RobotTelemetry {
    const val CONNECTION_ALERTS_TAB             : String = "Connection Alerts" // Usage: thisTab/Canbus/subsystemName + motor id
    const val ROBOT_MODE_TAB                    : String = "RobotMode"
    const val SUBSYSTEM_VISUALIZATION_2D_TAB    : String = "Subsystems Mechanism2d"
    const val SUBSYSTEM_VISUALIZATION_3D_TAB    : String = "Subsystems Pose3d"
}