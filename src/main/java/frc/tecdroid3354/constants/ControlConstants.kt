package frc.tecdroid3354.constants

import edu.wpi.first.units.AngularVelocityUnit
import edu.wpi.first.units.DistanceUnit
import edu.wpi.first.units.Units.DegreesPerSecond
import edu.wpi.first.units.Units.Seconds
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import frc.tecdroid3354.subsystems.angularVelocityTemplate.FlywheelConstants
import frc.tecdroid3354.utils.controlProfiles.AngularMotionTargets
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber
import frc.tecdroid3354.utils.devices.OpPositionControlRequests
import frc.tecdroid3354.utils.devices.OpPositionControlRequests.POSITION_TORQUE
import frc.tecdroid3354.utils.devices.OpVelocityControlRequests
import frc.tecdroid3354.utils.devices.OpVelocityControlRequests.VELOCITY_TORQUE
import frc.tecdroid3354.utils.inches
import frc.tecdroid3354.utils.rotationsPerMinute
import frc.tecdroid3354.utils.safety.MeasureLimits


/**
 * Each subsystem set of [frc.tecdroid3354.utils.safety.MeasureLimits]. Naming must be as follows:
 * subsystemName_limits
 */
object SubsystemsMovementLimits {
    val FLYWHEEL_VELOCITY_LIMITS: MeasureLimits<AngularVelocityUnit> =
        MeasureLimits(0.0.rotationsPerMinute .. 4_200.0.rotationsPerMinute)

    val ELEVATOR_DISPLACEMENT_LIMITS: MeasureLimits<DistanceUnit> =
        MeasureLimits(0.0.inches .. 52.0.inches)
}

object SubsystemsControlRequests {
    val FLYWHEEL_CONTROL_TYPE: OpVelocityControlRequests = VELOCITY_TORQUE
    val ELEVATOR_CONTROL_TYPE: OpPositionControlRequests = POSITION_TORQUE
}

object SubsystemsPresetTargets {
    val FLYWHEEL_PRESET_RPM: AngularVelocity = 1_800.0.rotationsPerMinute
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber]. Naming must be as follows:
 * subsystemName_motors_[kP/kI/kD/kF/manualTargetRPMs/manualTargetAngle/...]
 */
object SubsystemsTunableGains {
    //
    // FLYWHEEL ONLY
    //
    val FLYWHEEL_MOTORS_KP: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kP", 0.5)
    val FLYWHEEL_MOTORS_KI: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kI", 0.0)
    val FLYWHEEL_MOTORS_KD: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kD", 0.0)
    val FLYWHEEL_MOTORS_KF: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kF", 0.0)
    val FLYWHEEL_MOTORS_KS: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kS", 0.0)
    val FLYWHEEL_MOTORS_KV: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kV", 0.0)
    val FLYWHEEL_MOTORS_KA: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kA", 0.0)
    val FLYWHEEL_MANUAL_RPM: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/Manual RPMs", 1_800.0)

    //
    // ELEVATOR ONLY
    //
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.ControlGains]. Naming must be as follows:
 * subsystemName_[motor/system/feature/...]_gains
 */
object SubsystemsControlGains {
    val FLYWHEEL_MOTOR_GAINS: ControlGains = ControlGains(
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KP.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KI.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KD.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KF.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KS.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KV.get(),
        SubsystemsTunableGains.FLYWHEEL_MOTORS_KA.get(),
        g = 0.0 // Only because gravitational compensation wouldn't make sense in a flywheel.
    )
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.MotionTargets]. Naming must be as follows:
 * SUBSYSTEM_MOTION_TARGETS. In the case your subsystem has two sets of motion targets, specify the use case in the name
 */
object SubsystemsMotionTargets {
    val FLYWHEEL_MOTION_TARGETS: AngularMotionTargets =
        AngularMotionTargets(
            DegreesPerSecond.zero(),
            Seconds.zero(),
            Seconds.zero(),
        )
}
