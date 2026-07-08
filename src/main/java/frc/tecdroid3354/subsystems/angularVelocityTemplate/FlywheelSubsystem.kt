package frc.tecdroid3354.subsystems.angularVelocityTemplate

import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.Alert
import frc.robot.utils.subsystemUtils.generic.SysIdSubsystem
import frc.tecdroid3354.constants.SubsystemsTunableGains
import frc.tecdroid3354.utils.rotationsPerSecond
import org.littletonrobotics.junction.Logger

class FlywheelSubsystem(private val io: FlywheelIO) : SysIdSubsystem(FlywheelConstants.Telemetry.SUBSYSTEM_TAB) {
    // Auto generated file (by @AutoLog annotation in FlywheelIOInputs)
    private val inputs: FlywheelIOInputsAutoLogged = FlywheelIOInputsAutoLogged()

    /**
     * START OF SYS ID VARIABLES / METHODS
     */
    override val sysIdForwardRunningCondition: () -> Boolean
        get() = { true }
    override val sysIdBackwardRunningCondition: () -> Boolean
        get() = { true }
    override val motorPosition: Angle
        get() = { inputs.leadMotorPosition } as Angle
    override val motorVelocity: AngularVelocity
        get() = { inputs.leadMotorVelocity } as AngularVelocity
    override val power: Double
        get() = { inputs.leadMotorPower } as Double

    override fun setVoltage(voltage: Voltage) {
        io.setFlywheelSysIdMotorsVoltage(voltage)
    }
    /**
     * END OF SYS ID VARIABLES / METHODS
     */

    /**
     * START OF CONNECTION ALERT VARIABLES. These alerts are published separately from other inputs.
     * This is to make sure all connection alerts are found in a shared folder.
     */
    private val leadMotorConnectionAlert: Alert =
        Alert(FlywheelConstants.Telemetry.LEAD_MOTOR_CONNECTION_ALERT_TAB, Alert.AlertType.kError)
    private val followerMotorConnectionAlert: Alert =
        Alert(FlywheelConstants.Telemetry.FOLLOWER_MOTOR_CONNECTION_ALERT_TAB, Alert.AlertType.kError)
    /**
     * END OF CONNECTION ALERT VARIABLES
     */

    init {
        io.initialMotorConfiguration()
    }

    override fun periodic() {
        // IMPORTANT: This must be the first line in periodic() so that all other methods work with fresh data.
        io.updateFlywheelInputs(inputs)

        // Logs every field to the specified directory. It can be seen live through Elastic & AdvantageScope.
        Logger.processInputs(FlywheelConstants.Telemetry.SUBSYSTEM_TAB, inputs)

        // Update motor alerts based on inputs.
        leadMotorConnectionAlert.set(inputs.isLeadMotorConnected)
        followerMotorConnectionAlert.set(inputs.isFollowerMotorConnected)

        // Check if PIDF coefficients were changed live and update the motors.
        if (SubsystemsTunableGains.flywheel_motors_kP.hasChanged(hashCode()) ||
            SubsystemsTunableGains.flywheel_motors_kI.hasChanged(hashCode()) ||
            SubsystemsTunableGains.flywheel_motors_kD.hasChanged(hashCode()) ||
            SubsystemsTunableGains.flywheel_motors_kF.hasChanged(hashCode())) {
            io.updateFlywheelMotorsPIDF(
                SubsystemsTunableGains.flywheel_motors_kP.get(),
                SubsystemsTunableGains.flywheel_motors_kI.get(),
                SubsystemsTunableGains.flywheel_motors_kD.get(),
                SubsystemsTunableGains.flywheel_motors_kF.get())
        }
        // Check if the manual target RPMs were changed live and update the target.
        if (SubsystemsTunableGains.flywheel_motors_manualTargetRPMs.hasChanged(hashCode())) {
            io.updateFlywheelManualVelocity(SubsystemsTunableGains.flywheel_motors_manualTargetRPMs.get()
                .div(60.0)
                .rotationsPerSecond)
        }
    }
}