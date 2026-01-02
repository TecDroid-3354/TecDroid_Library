package frc.template.core

enum class RobotMode {
    REAL,
    SIM,
    REPLAY
}

object RobotConstants {
    val robotMode: RobotMode = RobotMode.REAL
    val tuningMode: Boolean = false
}