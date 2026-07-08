package frc.tecdroid3354.constants

import frc.tecdroid3354.subsystems.angularVelocityTemplate.FlywheelConstants
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber


/**
 * Each subsystem set of [frc.tecdroid3354.utils.safety.MeasureLimits]. Naming must be as follows:
 * subsystemName_limits
 */
object SubsystemsMovementLimits {

}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber]. Naming must be as follows:
 * subsystemName_motors_[kP/kI/kD/kF/manualTargetRPMs/manualTargetAngle/...]
 */
object SubsystemsTunableGains {
    val flywheel_motors_kP: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kP", 0.5)
    val flywheel_motors_kI: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kI", 0.0)
    val flywheel_motors_kD: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kD", 0.0)
    val flywheel_motors_kF: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/kF", 0.0)
    val flywheel_motors_manualTargetRPMs: LoggedTunableNumber =
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/Manual RPMs", 1800.0)
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.ControlGains]. Naming must be as follows:
 * subsystemName_[motor/system/feature/...]_gains
 */
object SubsystemsControlGains {
    val flywheel_motor_gains = ControlGains(
        SubsystemsTunableGains.flywheel_motors_kP.get(),
        SubsystemsTunableGains.flywheel_motors_kI.get(),
        SubsystemsTunableGains.flywheel_motors_kD.get(),
        SubsystemsTunableGains.flywheel_motors_kF.get(),
        0.0, 0.0, 0.0, 0.0
    )
}
