package frc.template.interfaces

import edu.wpi.first.units.Measure
import edu.wpi.first.units.Unit
import edu.wpi.first.units.measure.Angle
import frc.template.utils.MeasureLimits

interface MotionSubsystems {
    val subsystemUnit: Unit
    var motorPosition: Angle
    var motorPower: Double
    val limits: MeasureLimits<Unit>

    fun setMotorPosition(targetPosition: Measure<Unit>) { motorPosition = limits.coerceIn(targetPosition) as Angle }
    fun setMotorPower(power: Double) { if (motorPosition as Measure<Unit> !in limits) return else motorPower = power}

}
