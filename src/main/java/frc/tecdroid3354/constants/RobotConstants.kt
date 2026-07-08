package frc.tecdroid3354.constants

enum class RobotMode {
    REAL,
    SIM,
    REPLAY
}

object RobotConstants {
    val robotMode: RobotMode = RobotMode.REAL
    val tuningMode: Boolean = false
}

object CanBuses {
    const val MAIN_CANBUS: String = "rio"
    const val ALTERNATE_CANBUS: String = "canivore"
}

object RobotTelemetry {
    const val CONNECTION_ALERTS_TAB: String = "Connection Alerts"
}