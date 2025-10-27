package frc.template.utils.motors

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.controls.Follower
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.NeutralModeValue
import net.tecdroid.subsystems.util.motors.KrakenMotors
import net.tecdroid.util.NumericId
import net.tecdroid.util.hertz

class OpTalon(id: NumericId, canBusName: String) {

    val  motor = KrakenMotors.createDefaultTalon(id ,canBusName)

    init {
        optimizeMotorCan()
    }

    fun coast() {
        motor.setNeutralMode(NeutralModeValue.Coast)
    }

    fun brake() {
        motor.setNeutralMode(NeutralModeValue.Brake)
    }

    fun follow(leadingMotorController: TalonFX, opposeMaster: Boolean) {
        motor.setControl(Follower(leadingMotorController.deviceID, opposeMaster))
    }

    fun applyConfigAndClearFaults(config: TalonFXConfiguration) {
        motor.clearStickyFaults()
        motor.configurator.apply(config)
    }

    private fun optimizeMotorCan() {

        with(motor) {
            position.setUpdateFrequency(100.0.hertz)
            motorVoltage.setUpdateFrequency(100.0.hertz)
            acceleration.setUpdateFrequency(50.0.hertz)
            velocity.setUpdateFrequency(50.0.hertz)
            acceleration.setUpdateFrequency(25.0.hertz)
            controlMode.setUpdateFrequency(10.0.hertz)
        }
        motor.optimizeBusUtilization()
    }
}