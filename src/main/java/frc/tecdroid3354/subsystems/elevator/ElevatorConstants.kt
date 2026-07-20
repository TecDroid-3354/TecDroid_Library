package frc.tecdroid3354.subsystems.elevator

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.MotorAlignmentValue
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.units.DistanceUnit
import edu.wpi.first.units.measure.Distance
import frc.tecdroid3354.utils.mechanical.Reduction
import frc.tecdroid3354.utils.safety.MeasureLimits
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.controlProfiles.LinearMotionTargets
import frc.tecdroid3354.utils.Sprocket
import frc.tecdroid3354.utils.devices.KrakenMotors
import frc.tecdroid3354.utils.inches
import frc.tecdroid3354.utils.metersPerSecond
import frc.tecdroid3354.utils.seconds
import java.util.Optional

/**
 * Intended to contain ALL values that will not change without manual manipulation.
 * Separated into different objects to categorize.
 */
object ElevatorConstants {
    /**
     * All constants
     */
    object Identification {
        val elevatorCanBusName = "canBus"
        val leadMotorId = 0
        val followerMotorId = 0
    }
    /**
     * All constants that have physical contact with the elevator
     * All values are placeholders and must be tuned for your specific robot.
     */
    object Mechanical {
        val reduction: Reduction = Reduction(1.0)                   // Gear ratio motor - elevator
        val sprocket: Sprocket = Sprocket.fromRadius(2.0.inches)    // Converts rotational motion into linear motion
    }

    /**
     * All constants that dictate the behavior of the elevator
     * All values are placeholders and must be tuned for your specific robot.
     */
    object Control {                                                                // To enforce physical limits
        val gains: ControlGains = ControlGains()                // Loop control (PIDF) and Feedforward (SVAG) values
        val motionTargets: LinearMotionTargets = LinearMotionTargets(0.0.metersPerSecond, 0.0.seconds, 0.0.seconds)
                                                                // Cruise Velocity, Acceleration & Jerk
    }

    object TalonFXMotors {
        private val defaultNeutralMode: NeutralModeValue = NeutralModeValue.Brake
        private val invertedValue: InvertedValue = InvertedValue.CounterClockwise_Positive

        val followerAlignmentValue: MotorAlignmentValue = MotorAlignmentValue.Aligned

        var motorsConfig: TalonFXConfiguration = KrakenMotors.createTalonFXConfiguration(
            Optional.of(KrakenMotors.configureMotorOutputs(defaultNeutralMode, invertedValue)),
            Optional.empty(), // Stay with the default CurrentLimits configuration
            Optional.of(KrakenMotors.configureSlot0(Control.gains)),
            Optional.empty(),
            Optional.of(KrakenMotors.configureLinearMotionMagic(Control.motionTargets,
                                                                Mechanical.reduction, Mechanical.sprocket))
        )

    }

    object LogTable {
        val subsystemFolder = "Elevator"
    }
}