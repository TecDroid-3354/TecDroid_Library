package frc.template.subsystems.elevator

import com.ctre.phoenix6.controls.VoltageOut
import edu.wpi.first.units.Units.Meters
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.MutDistance
import edu.wpi.first.units.measure.Voltage
import frc.template.utils.devices.OpTalonFX
import frc.template.subsystems.elevator.ElevatorConstants.Identification
import frc.template.subsystems.elevator.ElevatorConstants.Mechanical
import frc.template.utils.volts

/**
 * [ElevatorIO] implementation intended to act as the I/O layer between the [Elevator] and two
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
    private val voltageRequest : VoltageOut = VoltageOut(0.0.volts)
    private var elevatorTargetDisplacement : MutDistance = Meters.mutable(0.0)

    /**
     * Called after the primary constructor. Makes sure the motors are configured.
     */
    init {
        leadMotorController.applyConfigAndClearFaults(ElevatorConstants.TalonFXMotors.motorsConfig)
        followerMotorController.applyConfigAndClearFaults(ElevatorConstants.TalonFXMotors.motorsConfig)

        followerMotorController.follow(leadMotorController.getMotorInstance(),
                                        ElevatorConstants.TalonFXMotors.followerOpposesMaster)
    }

    /**
     * Updates every field of the [ElevatorIOInputsAutoLogged] with the hardware signals of this cycle.
     * @param inputs The generated [ElevatorIOInputsAutoLogged] object keeping track of everything.
     */
    override fun updateInputs(inputs: ElevatorIO.ElevatorIOInputs) {
        inputs.elevatorDisplacement.mut_replace(convertMotorToSubsystemDisplacement(leadMotorController.position.value))
        inputs.elevatorTargetDisplacement.mut_replace(elevatorTargetDisplacement)

        inputs.isLeadMotorConnected = leadMotorController.isConnected.invoke()
        inputs.leadMotorPosition.mut_replace(leadMotorController.position.value)

        inputs.leadMotorVelocity.mut_replace(leadMotorController.velocity.value)
        inputs.leadMotorOutputVoltage.mut_replace(leadMotorController.outputVoltage.value)
        inputs.leadMotorSupplyCurrent.mut_replace(leadMotorController.supplyCurrent.value)

        inputs.isFollowerMotorConnected = followerMotorController.isConnected.invoke()
        inputs.followerMotorOutputVoltage.mut_replace(followerMotorController.outputVoltage.value)
        inputs.followerMotorSupplyCurrent.mut_replace(followerMotorController.supplyCurrent.value)
    }

    /**
     * Used for SysId characterization. Sets the voltage to the lead motor -and by consequence the follower motor.
     * The voltage value must be clamped within `[-12.0, 12.0]` before giving it to the implementations.
     * @param voltage The desired voltage.
     */
    override fun setElevatorMotorsVoltage(voltage: Voltage) {
        leadMotorController.voltageRequest(voltage)
    }

    /**
     * Changes the target displacement of the elevator (telemetry purposes) and commands the lead motor
     * after converting the [elevatorTargetDisplacement] to a motor position.
     * Target displacement must be clamped within limits before passing it to the implementation.
     * @param elevatorTargetDisplacement The desired displacement for the elevator.
     */
    override fun setElevatorTargetDisplacement(elevatorTargetDisplacement: Distance) {
        this.elevatorTargetDisplacement.mut_replace(elevatorTargetDisplacement)
        // MotionMagicVoltage request being used. MotionMagic has to be configured for this to work.
        leadMotorController.positionRequest(convertSubsystemToMotorPosition(elevatorTargetDisplacement))
    }

    /**
     * Only function (alongside its inverse, [convertMotorToSubsystemDisplacement]), is the only place where
     * some sort of logic is performed in the I/O, yet it still uses only components from [ElevatorConstants]
     * and the position of the lead motor.
     * TODO() = Can I move this outside of the implementation?
     * @param elevatorDisplacement The current displacement of the elevator.
     */
    private fun convertSubsystemToMotorPosition(elevatorDisplacement: Distance): Angle {
        return Mechanical.reduction.unapply(
            Mechanical.sprocket.linearDisplacementToAngularDisplacement(elevatorDisplacement)
        )
    }

    /**
     * Only function (alongside its inverse, [convertSubsystemToMotorPosition]), is the only place where
     * some sort of logic is performed in the I/O, yet it still uses only components from [ElevatorConstants]
     * and the position of the lead motor. Only place to use this: [updateInputs].
     * TODO() = Can I move this outside of the implementation?
     * @param motorPosition The current position of the lead motor.
     */
    private fun convertMotorToSubsystemDisplacement(motorPosition: Angle): Distance {
        return Mechanical.sprocket.angularDisplacementToLinearDisplacement(
            Mechanical.reduction.apply(motorPosition)
        )
    }

    /**
     * Uses the lead motor position as reference.
     * @return an [Angle] containing the lead motor position.
     */
    override fun getElevatorMotorPosition(): Angle {
        return leadMotorController.position.value
    }

    /**
     * Uses the lead motor position as reference.
     * @return an [AngularVelocity] containing the lead motor velocity.
     */
    override fun getElevatorMotorVelocity(): AngularVelocity {
        return leadMotorController.velocity.value
    }

    /**
     * Uses the lead motor position as reference.
     * Power in this context refers to a normalized factor representing the speed of the motor.
     * @return a value within `[-1.0, 1.0]` representing the power of the motor.
     */
    override fun getElevatorMotorPower(): Double {
        return leadMotorController.power.invoke()
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