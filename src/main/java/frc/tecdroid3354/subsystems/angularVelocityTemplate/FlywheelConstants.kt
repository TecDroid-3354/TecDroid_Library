package frc.tecdroid3354.subsystems.angularVelocityTemplate

import frc.tecdroid3354.constants.CanBuses
import frc.tecdroid3354.constants.RobotConstants
import frc.tecdroid3354.constants.RobotTelemetry

object FlywheelConstants {
    object Identification {
        const val FLYWHEEL_CANBUS_NAME: String = CanBuses.MAIN_CANBUS
        const val LEAD_MOTOR_ID: Int = 0
        const val FOLLOWER_MOTOR_ID: Int = 1
    }

    object MotorConfiguration {

    }

    object Telemetry {
        const val SUBSYSTEM_TAB: String = "Flywheel"
        const val LEAD_MOTOR_CONNECTION_ALERT_TAB: String =
            "${RobotTelemetry.CONNECTION_ALERTS_TAB}/${SUBSYSTEM_TAB}/Lead Motor id=${Identification.LEAD_MOTOR_ID}"
        const val FOLLOWER_MOTOR_CONNECTION_ALERT_TAB: String =
            "${RobotTelemetry.CONNECTION_ALERTS_TAB}/${SUBSYSTEM_TAB}/Follower Motor id=${Identification.FOLLOWER_MOTOR_ID}"
    }
}
