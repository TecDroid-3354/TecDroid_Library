package frc.template.interfaces

import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import net.tecdroid.subsystems.util.generic.TdSubsystem
import net.tecdroid.util.volts
import java.util.function.Supplier

interface VoltageControlledSubsystem {
    fun setVoltage(voltage: Voltage)
    fun setVoltageCommand(voltage: Supplier<Voltage>): Command = Commands.runOnce(
        { setVoltage(voltage.get()) },
        this as? TdSubsystem ?: throw IllegalStateException("Attempted to set voltage output on a non-subsystem.")
    )

    fun stop() = setVoltage(0.0.volts)
    fun stopCommand(): Command = Commands.runOnce(
        ::stop,
        this as? TdSubsystem
            ?: throw IllegalStateException("Attempted to stop a non-subsystem. This has no safety guarantees.")
    )
}