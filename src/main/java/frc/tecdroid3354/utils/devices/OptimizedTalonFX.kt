package frc.tecdroid3354.utils.devices

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.controls.Follower
import com.ctre.phoenix6.controls.MotionMagicExpoTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage
import com.ctre.phoenix6.controls.MotionMagicVoltage
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.MotorAlignmentValue
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.units.AngleUnit
import edu.wpi.first.units.DistanceUnit
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.Current
import edu.wpi.first.units.measure.Distance
import edu.wpi.first.units.measure.LinearVelocity
import edu.wpi.first.units.measure.Temperature
import edu.wpi.first.units.measure.Voltage
import frc.tecdroid3354.utils.Sprocket
import frc.tecdroid3354.utils.hertz
import frc.tecdroid3354.utils.mechanical.Reduction
import frc.tecdroid3354.utils.safety.MeasureLimits

// TODO() = Make this class an interface and implement it in OpTalonFXLinear, OpTalonFXAngular for modularity??
class OpTalonFX(private val id: Int, private val canBusName: String = "rio") {

    private val motor = KrakenMotors.createDefaultTalon(id ,canBusName)
    private var isFollower: Boolean = false

    private val voltageOutRequest = VoltageOut(0.0)
    private val motionMagicPositionVoltageRequest = MotionMagicVoltage(0.0)
    private val motionMagicPositionTorqueRequest = MotionMagicTorqueCurrentFOC(0.0)
    private val motionMagicPositionExpoTorqueRequest = MotionMagicExpoTorqueCurrentFOC(0.0)
    private val motionMagicVelocityVoltageRequest = MotionMagicVelocityVoltage(0.0)
    private val motionMagicVelocityTorqueRequest = MotionMagicVelocityTorqueCurrentFOC(0.0)

    private val COMMAND_TO_FOLLOWER_TALON_EXCEPTION = IllegalCallerException("Tried to command a follower TalonFX [$id]. Use the lead TalonFX.")

    init {
        optimizeMotorCan()
    }

    fun getPosition(): Angle { return motor.position.value }
    fun getVelocity(): AngularVelocity { return motor.velocity.value }
    fun getTemperature(): Temperature { return motor.deviceTemp.value }
    fun getOutputVoltage(): Voltage { return motor.motorVoltage.value }
    fun getSupplyCurrent(): Current { return motor.supplyCurrent.value }
    fun getTorqueCurrent(): Current { return motor.torqueCurrent.value }
    fun getPower(): Double { return motor.get() }
    fun getIsConnected(): Boolean { return motor.isConnected }

    fun voltageRequest(voltage: Voltage) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(voltageOutRequest.withOutput(voltage))
    }

    fun velocityVoltageRequest(velocity: AngularVelocity) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(motionMagicVelocityVoltageRequest.withVelocity(velocity))
    }

    fun velocityTorqueRequest(velocity: AngularVelocity) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(motionMagicVelocityTorqueRequest.withVelocity(velocity))
    }

    private fun positionVoltageRequest(position: Angle, slot: Int) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(motionMagicPositionVoltageRequest.withPosition(position).withSlot(slot))
    }

    private fun positionTorqueRequest(position: Angle, slot: Int) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(motionMagicPositionTorqueRequest.withPosition(position).withSlot(slot))
    }

    private fun positionExpoTorqueRequest(position: Angle, slot: Int) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.setControl(motionMagicPositionExpoTorqueRequest.withPosition(position).withSlot(slot))
    }

    fun angularSubsystemPositionVoltageRequest(position: Angle, limits: MeasureLimits<AngleUnit>, reduction: Reduction,
                                               slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedAngle: Angle = limits.coerceIn(position) as Angle
        val motorTransformedAngle: Angle = reduction.unapply(subsystemClampedAngle)
        positionVoltageRequest(motorTransformedAngle, slot)
    }

    fun angularSubsystemPositionTorqueRequest(position: Angle, limits: MeasureLimits<AngleUnit>, reduction: Reduction,
                                               slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedAngle: Angle = limits.coerceIn(position) as Angle
        val motorTransformedAngle: Angle = reduction.unapply(subsystemClampedAngle)
        positionVoltageRequest(motorTransformedAngle, slot)
    }

    fun angularSubsystemPositionExpoTorqueRequest(position: Angle, limits: MeasureLimits<AngleUnit>, reduction: Reduction,
                                               slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedAngle: Angle = limits.coerceIn(position) as Angle
        val motorTransformedAngle: Angle = reduction.unapply(subsystemClampedAngle)
        positionVoltageRequest(motorTransformedAngle, slot)
    }

    fun linearSubsystemPositionVoltageRequest(displacement: Distance, limits: MeasureLimits<DistanceUnit>, reduction: Reduction,
                                              sprocket: Sprocket, slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedDisplacement: Distance = limits.coerceIn(displacement) as Distance
        val requestedLinearDisplacementToAngularDisplacement: Angle =
            sprocket.linearDisplacementToAngularDisplacement(subsystemClampedDisplacement)
        val motorTransformedAngle: Angle = reduction.unapply(requestedLinearDisplacementToAngularDisplacement)
        positionVoltageRequest(motorTransformedAngle, slot)
    }

    fun linearSubsystemPositionTorqueRequest(displacement: Distance, limits: MeasureLimits<DistanceUnit>, reduction: Reduction,
                                              sprocket: Sprocket, slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedDisplacement: Distance = limits.coerceIn(displacement) as Distance
        val requestedLinearDisplacementToAngularDisplacement: Angle =
            sprocket.linearDisplacementToAngularDisplacement(subsystemClampedDisplacement)
        val motorTransformedAngle: Angle = reduction.unapply(requestedLinearDisplacementToAngularDisplacement)
        positionTorqueRequest(motorTransformedAngle, slot)
    }

    fun linearSubsystemPositionExpoTorqueRequest(displacement: Distance, limits: MeasureLimits<DistanceUnit>, reduction: Reduction,
                                              sprocket: Sprocket, slot: Int = 0) {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        val subsystemClampedDisplacement: Distance = limits.coerceIn(displacement) as Distance
        val requestedLinearDisplacementToAngularDisplacement: Angle =
            sprocket.linearDisplacementToAngularDisplacement(subsystemClampedDisplacement)
        val motorTransformedAngle: Angle = reduction.unapply(requestedLinearDisplacementToAngularDisplacement)
        positionExpoTorqueRequest(motorTransformedAngle, slot)
    }

    fun stopMotor() {
        if (isFollower) { throw COMMAND_TO_FOLLOWER_TALON_EXCEPTION }

        motor.stopMotor()
    }

    fun follow(leadingMotorController: TalonFX, opposeMaster: MotorAlignmentValue) {
        isFollower = true
        motor.setControl(Follower(leadingMotorController.deviceID, opposeMaster))
    }

    fun applyConfigAndClearFaults(config: TalonFXConfiguration) {
        motor.clearStickyFaults()
        motor.configurator.apply(config)
    }

    fun getMotorId(): Int {
        return motor.deviceID
    }

    fun getMotorInstance(): TalonFX {
        return motor
    }

    fun getMotorToAngularSubsystemPosition(reduction: Reduction): Angle {
        return reduction.apply(getPosition())
    }

    fun getMotorToAngularSubsystemVelocity(reduction: Reduction): AngularVelocity {
        return reduction.apply(getVelocity())
    }

    fun getMotorToLinearSubsystemDisplacement(reduction: Reduction, sprocket: Sprocket): Distance {
        return sprocket.angularDisplacementToLinearDisplacement(
            reduction.apply(getPosition())
        )
    }

    fun getMotorToLinearSubsystemVelocity(reduction: Reduction, sprocket: Sprocket): LinearVelocity {
        return sprocket.angularVelocityToLinearVelocity(
            reduction.apply(getVelocity())
        )
    }

    fun getAngularSubsystemToMotorPosition(reduction: Reduction, subsystemMeasurement: Angle): Angle {
        return reduction.unapply(subsystemMeasurement)
    }

    fun getAngularSubsystemToMotorVelocity(reduction: Reduction, subsystemMeasurement: AngularVelocity): AngularVelocity {
        return reduction.unapply(subsystemMeasurement)
    }

    fun getLinearSubsystemToMotorPosition(reduction: Reduction, sprocket: Sprocket, subsystemMeasurement: Distance): Angle {
        return reduction.unapply(
            sprocket.linearDisplacementToAngularDisplacement(subsystemMeasurement)
        )
    }

    fun getLinearSubsystemToMotorVelocity(reduction: Reduction, sprocket: Sprocket, subsystemMeasurement: LinearVelocity): AngularVelocity {
        return reduction.unapply(
            sprocket.linearVelocityToAngularVelocity(subsystemMeasurement)
        )
    }

    private fun optimizeMotorCan() {
        motor.optimizeBusUtilization()
        with(motor) {
            position.setUpdateFrequency(100.0.hertz)
            velocity.setUpdateFrequency(100.0.hertz)
            motorVoltage.setUpdateFrequency(100.0.hertz)    // Required by followers (Phoenix 6 documentation)
            supplyCurrent.setUpdateFrequency(100.0.hertz)
            acceleration.setUpdateFrequency(50.0.hertz)
            controlMode.setUpdateFrequency(100.0.hertz)
            dutyCycle.setUpdateFrequency(100.0.hertz)       // Required by followers (Phoenix 6 documentation)
            torqueCurrent.setUpdateFrequency(100.0.hertz)   // Required by followers (Phoenix 6 documentation)
            version.setUpdateFrequency(100.0.hertz)
        }
    }

    fun coast() {
        motor.setNeutralMode(NeutralModeValue.Coast)
    }

    fun brake() {
        motor.setNeutralMode(NeutralModeValue.Brake)
    }
}
