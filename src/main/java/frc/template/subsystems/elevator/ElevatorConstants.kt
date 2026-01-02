package frc.template.subsystems.elevator

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.units.DistanceUnit
import edu.wpi.first.units.measure.Distance
import frc.template.utils.mechanical.Reduction
import frc.template.utils.safety.MeasureLimits
import frc.template.utils.devices.CanId
import frc.template.utils.controlProfiles.ControlGains
import frc.template.utils.controlProfiles.LinearMotionTargets
import frc.template.utils.Sprocket
import frc.template.utils.controlProfiles.LoggedTunableNumber
import frc.template.utils.devices.KrakenMotors
import frc.template.utils.devices.RotationalDirection
import frc.template.utils.inches
import frc.template.utils.metersPerSecond
import frc.template.utils.seconds
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
        val leadMotorId = CanId(0)
        val followerMotorId = CanId(0)
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
    object Control {
        private val minimumDisplacement: Distance = 0.5.inches          // Minimum allowed displacement of the elevator
        private val maximumDisplacement: Distance = 52.0.inches         // Maximum allowed displacement of the elevator

        val limits: MeasureLimits<DistanceUnit> = MeasureLimits(minimumDisplacement, maximumDisplacement)
                                                                // To enforce physical limits
        val gains: ControlGains = ControlGains()                // Loop control (PIDF) and Feedforward (SVAG) values
        val motionTargets: LinearMotionTargets = LinearMotionTargets(0.0.metersPerSecond, 0.0.seconds, 0.0.seconds)
                                                                // Cruise Velocity, Acceleration & Jerk
    }

    object TalonFXMotors {
        private val defaultNeutralMode: NeutralModeValue = NeutralModeValue.Brake
        private val invertedValue: InvertedValue = RotationalDirection.Counterclockwise.toInvertedValue()

        val followerOpposesMaster: Boolean = false

        var motorsConfig: TalonFXConfiguration = KrakenMotors.createTalonFXConfiguration(
            Optional.of(KrakenMotors.configureMotorOutputs(defaultNeutralMode, invertedValue)),
            Optional.empty(), // Stay with the default CurrentLimits configuration
            Optional.of(KrakenMotors.configureSlot0(Control.gains)),
            Optional.of(KrakenMotors.configureLinearMotionMagic(Control.motionTargets,
                                                                Mechanical.reduction, Mechanical.sprocket))
        )

    }

    object LogTable {
        val subsystemFolder = "Elevator"
    }
}