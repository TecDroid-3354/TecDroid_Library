package frc.tecdroid3354.subsystems.angularVelocityTemplate

import edu.wpi.first.units.Units
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.Alert
import frc.robot.utils.subsystemUtils.generic.SysIdSubsystem
import frc.tecdroid3354.constants.SubsystemsTunableGains
import frc.tecdroid3354.utils.meters
import frc.tecdroid3354.utils.rotationsPerMinute
import org.littletonrobotics.junction.Logger
import kotlin.math.pow

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

    /**
     * Used for all sensors / actuators configuration.
     */
    init {
        io.initialMotorConfiguration()
    }

    /**
     * Updates and logs all inputs defined in [FlywheelIO.FlywheelIOInputs].
     * Updates connection alerts based on inputs.
     * Listens to updates through [frc.tecdroid3354.utils.controlProfiles.LoggedTunableNumber]
     */
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
            io.updateFlywheelManualVelocity(
                SubsystemsTunableGains.flywheel_motors_manualTargetRPMs.get().rotationsPerMinute)
        }
    }

    /**
     * Enables live-tuned velocity. See implementation comment for details.
     */
    fun enableFlywheelManualVelocity(): Runnable {
        return io.enableFlywheelManualVelocity()
    }

    /**
     * Enables pre-stored velocity. See implementation comment for details.
     */
    fun enableFlywheelPresetVelocity(): Runnable {
        return io.enableFlywheelPresetVelocity()
    }

    /**
     * Calls [getCalculatedFlywheelScoringVelocity], which is then fed to the I/O.
     *
     * See I/O implementation comment for details.
     * @param flywheelDistanceToTarget Differs from robot distance to target (odometry); account for offsets from robot center.
     */
    fun enableFlywheelCalculatedScoringVelocity(flywheelDistanceToTarget: Distance): Runnable {
        val flywheelCalculatedVelocity = getCalculatedFlywheelScoringVelocity(flywheelDistanceToTarget)

        return io.enableFlywheelCalculatedVelocity(flywheelCalculatedVelocity)
    }

    /**
     * Calls [getCalculatedFlywheelAssistVelocity], which is then fed to the I/O.
     *
     * See I/O implementation comment for details.
     * @param flywheelDistanceToTarget Differs from robot distance to target (odometry); account for offsets from robot center.
     */
    fun enableFlywheelCalculatedAssistVelocity(flywheelDistanceToTarget: Distance): Runnable {
        val flywheelCalculatedVelocity = getCalculatedFlywheelAssistVelocity(flywheelDistanceToTarget)

        return io.enableFlywheelCalculatedVelocity(flywheelCalculatedVelocity)
    }

    /**
     * Stops the flywheel. See implementation for details.
     */
    fun stopFlywheel(): Runnable {
        return io.stopFlywheel()
    }

    /**
     * Changes NeutralMode / IdleMode of the motors to Coast.
     */
    fun coastFlywheelMotors(): Runnable {
        return io.coastFlywheelMotors()
    }

    /**
     * Changes NeutralMode / IdleMode of the motors to Brake.
     */
    fun brakeFlywheelMotors(): Runnable {
        return io.brakeFlywheelMotors()
    }

    /**
     * Only if applicable. This is implemented here because it does not change between hardware / simulation layers.
     *
     *
     * Uses the stored scoring coefficients in [FlywheelConstants.PolynomialCoefficients] and evaluates
     * with the given flywheel distance to target.
     *
     *
     * Assumed Units:
     *
     *   - Distance: Meters
     *
     *   - Polynomial Output: Rotations Per Minute
     *
     * The output is divided by 60 before creating the [AngularVelocity] object, which accepts Rotations Per Second.
     * @param flywheelDistanceToTarget Differs from robot distance to target; account for offsets from robot center.
     * @return The [AngularVelocity] calculated by the scoring polynomial.
     */
    private fun getCalculatedFlywheelScoringVelocity(flywheelDistanceToTarget: Distance): AngularVelocity {
        val distanceInMeters = flywheelDistanceToTarget.meters
        val calculatedRPMs =
            FlywheelConstants.PolynomialCoefficients.SCORING_X3_COEFF * distanceInMeters.pow(3.0) +
                    FlywheelConstants.PolynomialCoefficients.SCORING_X2_COEFF * distanceInMeters.pow(2.0) +
                    FlywheelConstants.PolynomialCoefficients.SCORING_X1_COEFF * distanceInMeters +
                    FlywheelConstants.PolynomialCoefficients.SCORING_X0_COEFF

        return calculatedRPMs.rotationsPerMinute
    }

    /**
     * Only if applicable. This is implemented here because it does not change between hardware / simulation layers.
     *
     *
     * Uses the stored assist coefficients in [FlywheelConstants.PolynomialCoefficients] and evaluates
     * with the given flywheel distance to target.
     *
     *
     * Assumed Units:
     *
     *   - Distance: Meters
     *
     *   - Polynomial Output: Rotations Per Minute
     *
     * The output is divided by 60 before creating the [AngularVelocity] object, which accepts Rotations Per Second.
     * @param flywheelDistanceToTarget Differs from robot distance to target; account for offsets from robot center.
     * @return The [AngularVelocity] calculated by the assist polynomial.
     */
    private fun getCalculatedFlywheelAssistVelocity(flywheelDistanceToTarget: Distance): AngularVelocity {
        val distanceInMeters = flywheelDistanceToTarget.meters
        val calculatedRPMs =
            FlywheelConstants.PolynomialCoefficients.ASSIST_X3_COEFF * distanceInMeters.pow(3.0) +
                    FlywheelConstants.PolynomialCoefficients.ASSIST_X2_COEFF * distanceInMeters.pow(2.0) +
                    FlywheelConstants.PolynomialCoefficients.ASSIST_X1_COEFF * distanceInMeters +
                    FlywheelConstants.PolynomialCoefficients.ASSIST_X0_COEFF

        return calculatedRPMs.rotationsPerMinute
    }
}