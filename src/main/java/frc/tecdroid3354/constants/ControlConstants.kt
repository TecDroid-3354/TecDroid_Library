package frc.tecdroid3354.constants

import edu.wpi.first.units.AngularVelocityUnit
import edu.wpi.first.units.DistanceUnit
import edu.wpi.first.units.Units.DegreesPerSecond
import edu.wpi.first.units.Units.MetersPerSecond
import edu.wpi.first.units.Units.Seconds
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelConstants
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorConstants
import frc.tecdroid3354.utils.controlProfiles.AngularMotionTargets
import frc.tecdroid3354.utils.controlProfiles.LinearMotionTargets
import frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber
import frc.tecdroid3354.utils.controlProfiles.TunableControlGains
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.devices.OpPositionControlRequests
import frc.tecdroid3354.utils.devices.OpPositionControlRequests.POSITION_DYNAMIC_TORQUE
import frc.tecdroid3354.utils.devices.OpVelocityControlRequests
import frc.tecdroid3354.utils.devices.OpVelocityControlRequests.VELOCITY_TORQUE
import frc.tecdroid3354.utils.inches
import frc.tecdroid3354.utils.metersPerSecond
import frc.tecdroid3354.utils.rotationsPerMinute
import frc.tecdroid3354.utils.safety.MeasureLimits
import frc.tecdroid3354.utils.seconds

/**
 * Each subsystem set of [frc.tecdroid3354.utils.safety.MeasureLimits]. Naming must be as follows:
 * subsystemName_limits
 */
object SubsystemsMovementLimits {
    //
    // FLYWHEEL ONLY
    //
    val FLYWHEEL_VELOCITY_LIMITS: MeasureLimits<AngularVelocityUnit> =
        MeasureLimits(0.0.rotationsPerMinute .. 4_200.0.rotationsPerMinute)

    //
    // ELEVATOR ONLY
    //
    val ELEVATOR_DISPLACEMENT_LIMITS: MeasureLimits<DistanceUnit> =
        MeasureLimits(0.0.inches .. 52.0.inches)
}

/**
 * Stores an Enum value with the Control Request Type used for each subsystem.
 * The corresponding value must be called inside the subsystem's hardware layer when commanding the motor(s) to move.
 */
object SubsystemsControlRequests {
    val FLYWHEEL_CONTROL_TYPE: OpVelocityControlRequests = VELOCITY_TORQUE
    val ELEVATOR_CONTROL_TYPE: OpPositionControlRequests = POSITION_DYNAMIC_TORQUE
}

object SubsystemsPresetTargets {
    //
    // FLYWHEEL ONLY
    //
    val FLYWHEEL_PRESET_RPM: AngularVelocity = 3_200.0.rotationsPerMinute

    //
    // ELEVATOR ONLY
    //
    val ELEVATOR_IDLE_DISPLACEMENT: Distance = 10.0.inches
    val ELEVATOR_HOME_DISPLACEMENT: Distance = 0.0.inches
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber]. Naming must be as follows:
 * subsystemName_[kP/kI/kD/kF/manualTargetRPMs/manualTargetAngle/...]
 */
object SubsystemsTunableTargets {
    //
    // FLYWHEEL ONLY
    //
    val FLYWHEEL_MANUAL_RPM: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/Manual RPMs", 1_800.0)

    //
    // ELEVATOR ONLY
    //
    val ELEVATOR_MANUAL_TARGET_INCHES: LoggedTunableNumber =
        LoggedTunableNumber("${ ElevatorConstants.Telemetry.SUBSYSTEM_TAB }/Manual Target (in)", 8.0)
}

 /**
 * Each subsystem set of [TunableControlGains]. Note that this are still tunable, yet specifically for [ControlGains],
 * which is why they are here.
 *
 * Naming must be as follows:
 * ***SUBSYSTEM_MOTOR_[[PRIMARY/SECONDARY/TERTIARY]]_GAINS***, where ***[[PRIMARY/SECONDARY/TERTIARY]]***
 * refers to slot 0,1,2 configs in Phoenix.
 *
 * For readability, ensure you specify the argument name before the coefficients.
 */
object SubsystemsControlGains {
    //
    // FLYWHEEL ONLY
    //
     val FLYWHEEL_MOTOR_PRIMARY_GAINS: TunableControlGains = TunableControlGains(FlywheelConstants.Telemetry.SUBSYSTEM_PRIMARY_GAINS,
        kP = 10.0, kI = 0.0, kD = 0.0, kS = 0.0, kV = 0.0, kA = 0.0, kG = 0.0) // "Tuned" in SIMULATION -> Torque Request (Probably wrong MOI)
     val FLYWHEEL_MOTOR_SECONDARY_GAINS: TunableControlGains = TunableControlGains(FlywheelConstants.Telemetry.SUBSYSTEM_SECONDARY_GAINS,
         kP = 0.5, kI = 0.0, kD = 0.0, kS = 0.0, kV = 0.0, kA = 0.0, kG = 0.0) // Not Tuned

     //
     // ELEVATOR ONLY
     //
     val ELEVATOR_MOTOR_PRIMARY_GAINS: TunableControlGains = TunableControlGains(ElevatorConstants.Telemetry.SUBSYSTEM_PRIMARY_GAINS,
        kP = 450.0, kI = 0.0, kD = 10.0, kS = 0.0, kV = 0.0, kA = 0.75, kG = 6.52) // Tuned in SIMULATION -> Torque Request.
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.MotionTargets]. Naming must be as follows:
 * SUBSYSTEM_MOTION_TARGETS.
 *
 * For dynamic subsystems that require more than 1 set of motion targets, specify if the variable contains the
 * primary, secondary, or tertiary motion targets.
 */
object SubsystemsMotionTargets {
    //
    // FLYWHEEL ONLY
    //
    val FLYWHEEL_MOTION_TARGETS: AngularMotionTargets =
        AngularMotionTargets(
            4_500.0.rotationsPerMinute,
            0.1.seconds,
            Seconds.zero(),
        )

    //
    // ELEVATOR ONLY
    //
    val ELEVATOR_PRIMARY_MOTION_TARGETS: LinearMotionTargets = // Standard motion
        LinearMotionTargets(
            1.2.metersPerSecond,
            0.1.seconds,
            0.1.seconds,
        )

    val ELEVATOR_SECONDARY_MOTION_TARGETS: LinearMotionTargets = // For manual motion
        LinearMotionTargets( // Slower for showcase, still usable for tuning since both targets use Slot0
            0.8.metersPerSecond,
            0.8.seconds,
            0.5.seconds,
        )
}
