package frc.template.subsystems.elevator

import edu.wpi.first.math.MathUtil
import edu.wpi.first.units.Units.Volts
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.Alert
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.InstantCommand
import frc.template.utils.subsystemUtils.generic.TdSubsystem
import frc.template.utils.subsystemUtils.identification.GenericSysIdRoutine
import frc.template.utils.volts
import org.littletonrobotics.junction.Logger

/**
 * Intended to act as a bridge between the I/O layer and the rest of the program.
 * All logic regarding the elevator behaviour should be performed here, as the I/O must receive exclusively orders to
 * pass to either the hardware or simulation, depending on the implementation.
 */
class Elevator(private val io: ElevatorIO) : TdSubsystem(ElevatorConstants.LogTable.subsystemFolder) {
    /**
     * I/O (Input/Output) variables ([io] passed in constructor). Enables to use different implementations of
     * [ElevatorIO] without modifying this class.
     */
    private val inputs: ElevatorIOInputsAutoLogged = ElevatorIOInputsAutoLogged()

    /**
     * SysId variables. Origin of the variables: [TdSubsystem]
     *  - [sysIdForwardRunningCondition] checks the subsystem has not violated its maximum limit
     *  - [sysIdBackwardRunningCondition] checks the subsystem has not violated its minimum limit
     *  - [power] The elevator motors power. Categorized here since SysId is the only place where it's used.
     *  - [sysIdRoutines] contains all quasistatic and
     */
    override val sysIdForwardRunningCondition: () -> Boolean
        get() = { inputs.elevatorDisplacement.lt(ElevatorConstants.Control.limits.maximum) }

    override val sysIdBackwardRunningCondition: () -> Boolean
        get() = { inputs.elevatorDisplacement.gt(ElevatorConstants.Control.limits.minimum) }

    override val power: Double
        get() = io.elevatorMotorPower

    val sysIdRoutines: GenericSysIdRoutine = createIdentificationRoutine()  // Comes form TdSubsystem

    /**
     * Motor controller variables. lead motor is used as reference.
     * All data must be managed through the I/O layer and updated periodically.
     */
    override val motorPosition: Angle
        get() = io.elevatorMotorPosition

    override val motorVelocity: AngularVelocity
        get() = io.elevatorMotorVelocity

    /**
     * Alerts to inform driver / developers something went wrong
     */
    val leadMotorDisconnectedAlert: Alert =
        Alert("Elevator's lead motor lost connection", Alert.AlertType.kError)
    val followerMotorDisconnectedAlert: Alert =
        Alert("Elevator's follower motor lost connection", Alert.AlertType.kError)

    /**
     * Called every 20ms. Updates every input according to the I/O implementation and logs it.
     */
    override fun periodic() {
        io.updateInputs(inputs)
        // Make sure is AdvantageKit's Logger (org.littletonrobotics.junction) and not Java's.
        Logger.processInputs(ElevatorConstants.LogTable.subsystemFolder, inputs)

        // Updates each alert based on the retrieved connectivity status of this cycle.
        // alert = notConnected ? true : false
        leadMotorDisconnectedAlert.set(inputs.isLeadMotorConnected.not())
        followerMotorDisconnectedAlert.set(inputs.isFollowerMotorConnected.not())
    }

    /**
     * Clamps the desired voltage within `[-12.0, 12.0]` and passes the result
     * to the [io] layer to command the motors.
     * Intended to be used by [sysIdRoutines] only.
     * @param voltage The desired voltage.
     */
    override fun setVoltage(voltage: Voltage) {
        io.setElevatorMotorsVoltage(MathUtil.clamp(voltage.`in`(Volts), -12.0, 12.0).volts);
    }

    /**
     * Clamps the desired [targetDisplacement] within the limits defined in [ElevatorConstants] and
     * passes the result to the [io] layer to command the motors.
     * @param targetDisplacement The desired target displacement of the [Elevator] (NOT the motors).
     */
    fun setElevatorTargetDisplacement(targetDisplacement: Distance) {
        io.setElevatorTargetDisplacement(ElevatorConstants.Control.limits.coerceIn(targetDisplacement) as Distance);
    }

    /**
     * Fabricates an [InstantCommand] switching the Neutral / Idle mode of the motors to coast through the I/O layer.
     * @return an [InstantCommand] that coasts the [Elevator] motors.
     */
    fun coast(): Command {
        return InstantCommand({ io.coastElevatorMotors() } ).ignoringDisable(true);
    }

    /**
     * Fabricates an [InstantCommand] switching the Neutral / Idle mode of the motors to brake through the I/O layer.
     * @return an [InstantCommand] that brakes the [Elevator] motors.
     */
    fun brake(): Command {
        return InstantCommand({ io.brakeElevatorMotors() } ).ignoringDisable(true);
    }
}
