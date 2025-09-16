package frc.template.interfaces

import edu.wpi.first.units.measure.Angle
import frc.template.utils.ThroughBoreAbsoluteEncoder
import net.tecdroid.subsystems.util.generic.TdSubsystem

interface WithThroughBoreAbsoluteEncoder {
    val absoluteEncoder: ThroughBoreAbsoluteEncoder
    val absoluteAngle: Angle
        get() = absoluteEncoder.position

    fun onMatchRelativeEncodersToAbsoluteEncoders()

    fun matchRelativeEncodersToAbsoluteEncoders() {
        require(this is TdSubsystem) { "Classes With (a) Through Bore Absolute Encoder must be Subsystems" }
        onMatchRelativeEncodersToAbsoluteEncoders()
    }
}