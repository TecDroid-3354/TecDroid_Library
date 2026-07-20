package frc.tecdroid3354.subsystems.elevator

import edu.wpi.first.units.Units.Meters
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.MutDistance
import edu.wpi.first.units.measure.Voltage
import frc.tecdroid3354.constants.SubsystemsControlRequests
import frc.tecdroid3354.constants.SubsystemsMovementLimits
import frc.tecdroid3354.utils.devices.OpTalonFX
import frc.tecdroid3354.subsystems.elevator.ElevatorConstants.Identification
import frc.tecdroid3354.subsystems.elevator.ElevatorConstants.Mechanical

/**
 * [ElevatorIO] implementation intended to act as the I/O layer between the [ElevatorSubsystem] and two
 * [com.ctre.phoenix6.hardware.TalonFX] motor controllers.
 * No logic should be performed here, all methods will interact with the hardware exclusively with the information
 * passed as parameters.
 */
class ElevatorIOTalonFX : ElevatorIO {
    // Make sure to configure it.
    private val leadMotorController : OpTalonFX = OpTalonFX(Identification.leadMotorId,
                                                            Identification.elevatorCanBusName)
    // Make sure to configure it and set it as follower.
    private val followerMotorController : OpTalonFX = OpTalonFX(Identification.followerMotorId,
                                                            Identification.elevatorCanBusName)
    private var elevatorTargetDisplacement : MutDistance = Meters.mutable(0.0)

    /**
     * Called after the primary constructor. Makes sure the motors are configured.
     */
    init {
        leadMotorController.applyConfigAndClearFaults(ElevatorConstants.TalonFXMotors.motorsConfig)
        followerMotorController.applyConfigAndClearFaults(ElevatorConstants.TalonFXMotors.motorsConfig)

        followerMotorController.follow(leadMotorController.getMotorInstance(),
                                        ElevatorConstants.TalonFXMotors.followerAlignmentValue)
    }

    /**
     * Updates every field of the [ElevatorIOInputsAutoLogged] with the hardware signals of this cycle.
     * @param inputs The generated [ElevatorIOInputsAutoLogged] object keeping track of everything.
     */
    override fun updateInputs(inputs: ElevatorIO.ElevatorIOInputs) {
        inputs.elevatorDisplacement.mut_replace(leadMotorController.getMotorToLinearSubsystemDisplacement(
            Mechanical.reduction, Mechanical.sprocket
        ))
        inputs.elevatorTargetDisplacement.mut_replace(elevatorTargetDisplacement)

        inputs.isLeadMotorConnected = leadMotorController.getIsConnected()
        inputs.leadMotorPosition.mut_replace(leadMotorController.getPosition())

        inputs.leadMotorVelocity.mut_replace(leadMotorController.getVelocity())
        inputs.leadMotorOutputVoltage.mut_replace(leadMotorController.getOutputVoltage())
        inputs.leadMotorSupplyCurrent.mut_replace(leadMotorController.getSupplyCurrent())

        inputs.isFollowerMotorConnected = followerMotorController.getIsConnected()
        inputs.followerMotorOutputVoltage.mut_replace(followerMotorController.getOutputVoltage())
        inputs.followerMotorSupplyCurrent.mut_replace(followerMotorController.getSupplyCurrent())
    }

    /**
     * Used for SysId characterization. Sets the voltage to the lead motor -and by consequence the follower motor.
     * The voltage value must be clamped within `[-12.0, 12.0]` before giving it to the implementations.
     * @param voltage The desired voltage.
     */
    override fun setElevatorSysIdMotorsVoltage(voltage: Voltage) {
        leadMotorController.sysIdVoltageRequest(voltage)
    }

    /**
     * Changes the target displacement of the elevator (telemetry purposes) and commands the lead motor
     * after converting the [elevatorTargetDisplacement] to a motor position.
     * Target displacement must be clamped within limits before passing it to the implementation.
     * @param elevatorTargetDisplacement The desired displacement for the elevator.
     */
    override fun setElevatorTargetDisplacement(elevatorTargetDisplacement: Distance) {
        this.elevatorTargetDisplacement.mut_replace(elevatorTargetDisplacement)

        leadMotorController.linearSubsystemPositionRequest(
            SubsystemsControlRequests.ELEVATOR_CONTROL_TYPE,
            elevatorTargetDisplacement,
            SubsystemsMovementLimits.ELEVATOR_DISPLACEMENT_LIMITS,
            Mechanical.sprocket,
            Mechanical.reduction
        )
    }

    /**
     * Uses the lead motor position as reference.
     * @return an [Angle] containing the lead motor position.
     */
    override fun getElevatorMotorPosition(): Angle {
        return leadMotorController.getPosition()
    }

    /**
     * Uses the lead motor position as reference.
     * @return an [AngularVelocity] containing the lead motor velocity.
     */
    override fun getElevatorMotorVelocity(): AngularVelocity {
        return leadMotorController.getVelocity()
    }

    /**
     * Uses the lead motor position as reference.
     * Power in this context refers to a normalized factor representing the speed of the motor.
     * @return a value within `[-1.0, 1.0]` representing the power of the motor.
     */
    override fun getElevatorMotorPower(): Double {
        return leadMotorController.getPower()
    }

    /**
     * Merely changes the Neutral / Idle mode of the motors to coast for easier manipulation.
     */
    override fun coastElevatorMotors() {
        leadMotorController.coast()
        followerMotorController.coast()
    }

    /**
     * Merely changes the Neutral / Idle mode of the motors to brake to avoid unintended movement during match.
     */
    override fun brakeElevatorMotors() {
        leadMotorController.brake()
        followerMotorController.brake()
    }
}