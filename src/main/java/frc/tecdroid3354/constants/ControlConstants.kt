package frc.tecdroid3354.constants

import edu.wpi.first.units.Units.DegreesPerSecond
import edu.wpi.first.units.Units.Seconds
import frc.tecdroid3354.subsystems.angularVelocityTemplate.FlywheelConstants
import frc.tecdroid3354.utils.controlProfiles.AngularMotionTargets
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber
import frc.tecdroid3354.utils.rotationsPerMinute


/**
 * Each subsystem set of [frc.tecdroid3354.utils.safety.MeasureLimits]. Naming must be as follows:
 * subsystemName_limits
 */
object SubsystemsMovementLimits {

}

object SubsystemsPresetVelocities {
    val flywheel_preset_velocity = 1_800.0.rotationsPerMinute
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
        LoggedTunableNumber("${ FlywheelConstants.Telemetry.SUBSYSTEM_TAB }/Manual RPMs", 1_800.0)
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
        s = 0.0, v = 0.0, a = 0.0, g = 0.0
    )
}

/**
 * Each subsystem set of [frc.tecdroid3354.utils.controlProfiles.MotionTargets]. Naming must be as follows:
 * subsystemName_[angular/linear]_motion_targets
 */
object SubsystemsMotionTargets {
    val flywheel_angular_motion_targets: AngularMotionTargets =
        AngularMotionTargets(
            DegreesPerSecond.zero(),
            Seconds.zero(),
            Seconds.zero(),
        )
}
