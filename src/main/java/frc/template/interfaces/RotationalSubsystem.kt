package frc.template.interfaces

import edu.wpi.first.units.AngleUnit
import edu.wpi.first.units.Measure
import edu.wpi.first.units.Unit
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import frc.template.utils.MeasureLimits
import net.tecdroid.subsystems.util.generic.TdSubsystem

interface RotationalSubsystem: MotionSubsystems {
    var targetAngle: Angle
    var currentAngle: Angle
    var power: Double

    fun setTargetAngle(angle: Angle) { targetAngle = limits.coerceIn(angle) as Angle }
    fun setTargetAngleCommand(angle: Angle): Command {
        Commands.runOnce(
            { setTargetAngle(angle) },
            this.takeIf { this is TdSubsystem }.run {
                throw IllegalArgumentException("Attempted to run an angle command on a non-subsystem")
            }
        )
    }
    fun setPower(power: Double) { if (limits.contains(getCurrentAngle().times(1.2)).not()) return }
    fun setPowerCommand(power: Double): Command {
        Commands.runOnce(
            { setPower(power) },
            this.takeIf { this is TdSubsystem }.run {
                throw IllegalArgumentException("Attempted to run a power command on a non-subsystem")
            }
        )
    }

    fun getTargetAngle(): Angle = targetAngle
    fun getPower(): Double = power
    fun getCurrentAngle(): Angle = currentAngle

    fun matchEncodersWithAbsolute()
}