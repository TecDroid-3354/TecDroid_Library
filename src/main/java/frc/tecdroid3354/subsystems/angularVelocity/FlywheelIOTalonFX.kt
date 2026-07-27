package frc.tecdroid3354.subsystems.angularVelocity

import edu.wpi.first.math.MathUtil
import edu.wpi.first.units.Units.DegreesPerSecond
import edu.wpi.first.units.measure.AngularVelocity
import edu.wpi.first.units.measure.MutAngularVelocity
import edu.wpi.first.units.measure.Voltage
import frc.tecdroid3354.constants.SubsystemsControlGains
import frc.tecdroid3354.constants.SubsystemsControlRequests
import frc.tecdroid3354.constants.SubsystemsMovementLimits
import frc.tecdroid3354.constants.SubsystemsPresetTargets
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
        inputs.flywheelTargetVelocity.mut_replace(flywheelVelocityTarget)
        inputs.flywheelManualTargetVelocity.mut_replace(manualFlywheelVelocityTarget)
        inputs.flywheelPresetVelocity.mut_replace(SubsystemsPresetTargets.FLYWHEEL_PRESET_RPM)

        inputs.isLeadMotorConnected = leadMotorController.getIsConnected()
        inputs.leadMotorVelocity.mut_replace(leadMotorController.getVelocity())
        inputs.leadMotorAcceleration.mut_replace(leadMotorController.getAcceleration())
        inputs.leadMotorTemperature.mut_replace(leadMotorController.getTemperature())
        inputs.leadMotorOutputVoltage.mut_replace(leadMotorController.getOutputVoltage())
        inputs.leadMotorSupplyCurrent.mut_replace(leadMotorController.getSupplyCurrent())
        inputs.leadMotorTorqueCurrent.mut_replace(leadMotorController.getTorqueCurrent())

        inputs.isFollowerMotorConnected = followerMotorController.getIsConnected()
        inputs.followerMotorVelocity.mut_replace(followerMotorController.getVelocity())
        inputs.followerMotorAcceleration.mut_replace(followerMotorController.getAcceleration())
        inputs.followerMotorTemperature.mut_replace(followerMotorController.getTemperature())
        inputs.followerMotorOutputVoltage.mut_replace(followerMotorController.getOutputVoltage())
        inputs.followerMotorSupplyCurrent.mut_replace(followerMotorController.getSupplyCurrent())
        inputs.followerMotorTorqueCurrent.mut_replace(followerMotorController.getTorqueCurrent())
    }

    override fun updateFlywheelManualVelocity(newFlywheelManualVelocity: AngularVelocity) {
        manualFlywheelVelocityTarget.mut_replace(newFlywheelManualVelocity)
    }

    override fun updateFlywheelMotorsControlGains(slot: Int) {
        // Make sure the selected slot is either 0, 1, or 2
        val validatedSlot = MathUtil.clamp(slot, 0, 2)
        // Clone the initial config
        val newMotorsConfig = FlywheelConstants.PhoenixMotorConfiguration.initialMotorsConfiguration.clone()

        when (validatedSlot) { // Update the corresponding Slot Configs
            0 -> {
                newMotorsConfig.Slot0 = SubsystemsControlGains.FLYWHEEL_MOTOR_PRIMARY_GAINS.updatePhoenixSlot0Configs()
            }
            1 -> {
                newMotorsConfig.Slot1 = SubsystemsControlGains.FLYWHEEL_MOTOR_SECONDARY_GAINS.updatePhoenixSlot1Configs()
            }
            else -> { // Can assume else {} branch to be 2, but defaults to primary since tertiary are not declared.
                newMotorsConfig.Slot2 = SubsystemsControlGains.FLYWHEEL_MOTOR_PRIMARY_GAINS.updatePhoenixSlot2Configs()
            }
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
        leadMotorController.applyConfigAndClearFaults(FlywheelConstants.PhoenixMotorConfiguration.initialMotorsConfiguration)
        followerMotorController.applyConfigAndClearFaults(FlywheelConstants.PhoenixMotorConfiguration.initialMotorsConfiguration)

        followerMotorController.follow(
            leadMotorController.getMotorInstance(),
            FlywheelConstants.PhoenixMotorConfiguration.followerMotorAlignment)
    }
}