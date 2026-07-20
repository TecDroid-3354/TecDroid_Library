package frc.tecdroid3354.subsystems.angularVelocityTemplate

import com.ctre.phoenix6.configs.CurrentLimitsConfigs
import com.ctre.phoenix6.configs.MotionMagicConfigs
import com.ctre.phoenix6.configs.MotorOutputConfigs
import com.ctre.phoenix6.configs.Slot0Configs
import com.ctre.phoenix6.configs.Slot1Configs
import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.MotorAlignmentValue
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.units.measure.Current
import frc.tecdroid3354.constants.CanBuses
import frc.tecdroid3354.constants.RobotTelemetry
import frc.tecdroid3354.constants.SubsystemsControlGains
import frc.tecdroid3354.constants.SubsystemsMotionTargets
import frc.tecdroid3354.utils.amps
import frc.tecdroid3354.utils.devices.KrakenMotors
import frc.tecdroid3354.utils.mechanical.Reduction
import java.util.Optional

object FlywheelConstants {
    /**
     * Contains the ID Of any hardware related to the subsystem and the CANBUS it is on
     */
    object Identification {
        const val FLYWHEEL_CANBUS_NAME: String = CanBuses.MAIN_CANBUS
        const val LEAD_MOTOR_ID: Int = 0
        const val FOLLOWER_MOTOR_ID: Int = 1
    }

    /**
     * Only for gear ratio ([Reduction]). In the case of linear subsystems, the sprocket also goes here.
     */
    object Mechanical {
        val REDUCTION: Reduction = Reduction(1.0)
    }

    /**
     * Only in the scenario you use a polynomial to calculate the target flywheel velocity.
     * In this example we assume two polynomials, one for scoring and another for assist (based off 2026 REBUILT)
     */
    object PolynomialCoefficients {
        const val SCORING_X3_COEFF: Double = 0.0
        const val SCORING_X2_COEFF: Double = 0.0
        const val SCORING_X1_COEFF: Double = 0.0
        const val SCORING_X0_COEFF: Double = 0.0

        const val ASSIST_X3_COEFF: Double = 0.0
        const val ASSIST_X2_COEFF: Double = 0.0
        const val ASSIST_X1_COEFF: Double = 0.0
        const val ASSIST_X0_COEFF: Double = 0.0
    }

    /**
     * Contains initial configuration for the Flywheel motors.
     * Configurations meant to be tunable live, limits, control gains, motion targets and movement presets
     * are all stored in a separate file where they are next to those of all other subsystems (excluding drivetrain).
     * This structure is to have a single file that is regularly consulted by the Software Team, whereas this one
     * remains mostly untouched unless the Design or Electrical Teams change something.
     */
    object MotorConfiguration {
        val followerMotorAlignment: MotorAlignmentValue = MotorAlignmentValue.Aligned

        private val neutralMode: NeutralModeValue = NeutralModeValue.Coast
        private val motorDirection: InvertedValue = InvertedValue.CounterClockwise_Positive

        private val supplyCurrentLimit: Current = 30.0.amps
        private val statorCurrentLimit: Current = 60.0.amps

        val initialMotorsConfiguration: TalonFXConfiguration = KrakenMotors.createTalonFXConfiguration(
            Optional.of<MotorOutputConfigs>(
                KrakenMotors.configureMotorOutputs(neutralMode, motorDirection)
            ),
            Optional.of<CurrentLimitsConfigs>(
                KrakenMotors.configureCurrentLimits(supplyCurrentLimit, statorCurrentLimit)
            ),
            Optional.of<Slot0Configs>(
                KrakenMotors.configureSlot0(SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS)
            ),
            Optional.empty<Slot1Configs>(),
            Optional.of<MotionMagicConfigs>(
                KrakenMotors.configureAngularMotionMagic(
                    SubsystemsMotionTargets.FLYWHEEL_MOTION_TARGETS,
                    Mechanical.REDUCTION))
        )

    }

    /**
     * Merely contains the folder names for different Telemetry tabs.
     *
     * **NOTE:** All alerts share a common parent folder defined in [RobotTelemetry.CONNECTION_ALERTS_TAB] for
     * easier alert visualization in Elastic.
     */
    object Telemetry {
        const val SUBSYSTEM_TAB: String = "Flywheel"
        const val LEAD_MOTOR_CONNECTION_ALERT_TAB: String =
            "${RobotTelemetry.CONNECTION_ALERTS_TAB}/${SUBSYSTEM_TAB}/Lead Motor id=${Identification.LEAD_MOTOR_ID}"
        const val FOLLOWER_MOTOR_CONNECTION_ALERT_TAB: String =
            "${RobotTelemetry.CONNECTION_ALERTS_TAB}/${SUBSYSTEM_TAB}/Follower Motor id=${Identification.FOLLOWER_MOTOR_ID}"
    }
}
