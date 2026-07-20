package frc.tecdroid3354.subsystems.angularVelocityTemplate

import edu.wpi.first.math.MathUtil
import edu.wpi.first.units.Units.DegreesPerSecond
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.MutAngularVelocity
import edu.wpi.first.units.measure.Voltage
import frc.tecdroid3354.constants.SubsystemsControlGains
import frc.tecdroid3354.constants.SubsystemsControlRequests
import frc.tecdroid3354.constants.SubsystemsMovementLimits
import frc.tecdroid3354.constants.SubsystemsPresetTargets
import frc.tecdroid3354.utils.controlProfiles.ControlGains
import frc.tecdroid3354.utils.devices.KrakenMotors
import frc.tecdroid3354.utils.devices.OpTalonFX

/**
 * Hardware layer for TalonFX motor controllers. Only file where [com.ctre.phoenix6.hardware.TalonFX]
 * motors are instantiated for this subsystem.
 *
 * **NOTE:** All methods implemented from the interface will inherit their comments. It is not necessary to repeat
 * those comments here.
 */
class FlywheelIOTalonFX: FlywheelIO {
    private val leadMotorController: OpTalonFX = OpTalonFX(
        FlywheelConstants.Identification.LEAD_MOTOR_ID,
        FlywheelConstants.Identification.FLYWHEEL_CANBUS_NAME)
    private val followerMotorController: OpTalonFX = OpTalonFX(
        FlywheelConstants.Identification.FOLLOWER_MOTOR_ID,
        FlywheelConstants.Identification.FLYWHEEL_CANBUS_NAME)

    /**
     * Note that [flywheelVelocityTarget] may contain the same value as [manualFlywheelVelocityTarget] when
     * [enableFlywheelManualVelocity] is commanded.
     */
    private val manualFlywheelVelocityTarget: MutAngularVelocity = DegreesPerSecond.mutable(0.0)
    private val flywheelVelocityTarget: MutAngularVelocity = DegreesPerSecond.mutable(0.0)

    @Suppress("DuplicatedCode")
    override fun updateFlywheelInputs(inputs: FlywheelIO.FlywheelIOInputs) {
        inputs.flywheelActualVelocity.mut_replace(leadMotorController.getMotorToAngularSubsystemVelocity(
            FlywheelConstants.Mechanical.REDUCTION
        ))
        inputs.flywheelPresetVelocity.mut_replace(SubsystemsPresetTargets.FLYWHEEL_PRESET_RPM)
        inputs.flywheelTargetVelocity.mut_replace(flywheelVelocityTarget)

        inputs.isLeadMotorConnected = leadMotorController.getIsConnected()
        inputs.leadMotorPosition.mut_replace(leadMotorController.getPosition())
        inputs.leadMotorVelocity.mut_replace(leadMotorController.getVelocity())
        inputs.leadMotorTemperature.mut_replace(leadMotorController.getTemperature())
        inputs.leadMotorOutputVoltage.mut_replace(leadMotorController.getOutputVoltage())
        inputs.leadMotorSupplyCurrent.mut_replace(leadMotorController.getSupplyCurrent())
        inputs.leadMotorTorqueCurrent.mut_replace(leadMotorController.getTorqueCurrent())
        inputs.leadMotorPower = leadMotorController.getPower()

        inputs.isFollowerMotorConnected = followerMotorController.getIsConnected()
        inputs.followerMotorPosition.mut_replace(followerMotorController.getPosition())
        inputs.followerMotorVelocity.mut_replace(followerMotorController.getVelocity())
        inputs.followerMotorTemperature.mut_replace(followerMotorController.getTemperature())
        inputs.followerMotorOutputVoltage.mut_replace(followerMotorController.getOutputVoltage())
        inputs.followerMotorSupplyCurrent.mut_replace(followerMotorController.getSupplyCurrent())
        inputs.followerMotorTorqueCurrent.mut_replace(followerMotorController.getTorqueCurrent())
        inputs.followerMotorPower = followerMotorController.getPower()
    }

    override fun updateFlywheelManualVelocity(newFlywheelManualVelocity: AngularVelocity) {
        manualFlywheelVelocityTarget.mut_replace(newFlywheelManualVelocity)
    }

    override fun updateFlywheelMotorsPIDF(kP: Double, kI: Double, kD: Double, kF: Double, slot: Int) {
        // Make sure the selected slot is either 0 or 1
        val validatedSlot = MathUtil.clamp(slot, 0, 1)
        // Clone the initial config
        val newMotorsConfig = FlywheelConstants.MotorConfiguration.initialMotorsConfiguration.clone()

        if (validatedSlot == 0) { // Update the corresponding Slot Configs
            newMotorsConfig.Slot0 = KrakenMotors.configureSlot0(
                ControlGains(kP, kI, kD, kF,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.s,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.v,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.a,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.g),
            )
        } else {
            newMotorsConfig.Slot1 = KrakenMotors.configureSlot1(
                ControlGains(kP, kI, kD, kF,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.s,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.v,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.a,
                    SubsystemsControlGains.FLYWHEEL_MOTOR_GAINS.g),
            )
        }

        leadMotorController.applyConfigAndClearFaults(newMotorsConfig)
        followerMotorController.applyConfigAndClearFaults(newMotorsConfig)
    }

    override fun enableFlywheelManualVelocity(): Runnable {
        return {
            flywheelVelocityTarget.mut_replace(manualFlywheelVelocityTarget) // Update target velocity

            leadMotorController.angularSubsystemVelocityRequest(
                SubsystemsControlRequests.FLYWHEEL_CONTROL_TYPE,
                manualFlywheelVelocityTarget,
                SubsystemsMovementLimits.FLYWHEEL_VELOCITY_LIMITS,
                FlywheelConstants.Mechanical.REDUCTION
            )
        }
    }

    override fun enableFlywheelPresetVelocity(): Runnable {
        return {
            flywheelVelocityTarget.mut_replace(SubsystemsPresetTargets.FLYWHEEL_PRESET_RPM) // Update target velocity

            leadMotorController.angularSubsystemVelocityRequest(
                SubsystemsControlRequests.FLYWHEEL_CONTROL_TYPE,
                SubsystemsPresetTargets.FLYWHEEL_PRESET_RPM,
                SubsystemsMovementLimits.FLYWHEEL_VELOCITY_LIMITS,
                FlywheelConstants.Mechanical.REDUCTION
            )
        }
    }
    override fun enableFlywheelCalculatedVelocity(flywheelCalculatedVelocity: AngularVelocity): Runnable {
        return {
            flywheelVelocityTarget.mut_replace(flywheelCalculatedVelocity) // Update target velocity

            leadMotorController.angularSubsystemVelocityRequest(
                SubsystemsControlRequests.FLYWHEEL_CONTROL_TYPE,
                flywheelCalculatedVelocity,
                SubsystemsMovementLimits.FLYWHEEL_VELOCITY_LIMITS,
                FlywheelConstants.Mechanical.REDUCTION
            )
        }
    }

    override fun stopFlywheel(): Runnable {
        return {
            leadMotorController.stopMotor()
        }
    }

    override fun setFlywheelSysIdMotorsVoltage(voltage: Voltage): Runnable {
        return {
            leadMotorController.sysIdVoltageRequest(voltage)
        }
    }

    override fun coastFlywheelMotors(): Runnable {
        return {
            leadMotorController.coast()
            followerMotorController.coast()
        }
    }

    override fun brakeFlywheelMotors(): Runnable {
        return {
            leadMotorController.brake()
            followerMotorController.brake()
        }
    }

    override fun initialMotorConfiguration() {
        leadMotorController.applyConfigAndClearFaults(FlywheelConstants.MotorConfiguration.initialMotorsConfiguration)
        followerMotorController.applyConfigAndClearFaults(FlywheelConstants.MotorConfiguration.initialMotorsConfiguration)

        followerMotorController.follow(
            leadMotorController.getMotorInstance(),
            FlywheelConstants.MotorConfiguration.followerMotorAlignment)
    }
}