package frc.tecdroid3354.subsystems.angularVelocityTemplate;

import edu.wpi.first.units.measure.*;
import org.littletonrobotics.junction.AutoLog;

import static edu.wpi.first.units.Units.*;

/**
 * I/O (Input/Output) interface intended for any {@link AngularVelocity} driven subsystem, i.e. a Flywheel.
 * Duplicate the motor-related fields inside {@link FlywheelIOInputs} for the number of motors in your subsystem,
 * or delete the follower motor fields if your subsystem is single-motor.
 * <p>
 * Any changes to this or any template present in the repository must be discussed with the area Lead(s) and Captain.
 * </p>
 * <p>
 * Why is this file in Java if our primary language is Kotlin?
 *  <p>
 *  - Java annotations (i.e. {@link org.littletonrobotics.junction.AutoLog @AutoLog}) are not present in kotlin.
 *      This is solvable using kapt, yet having this sort of bridge can make builds / deploys take significantly longer.
 *      Note that kapt is still being used, yet only to read the {@link org.littletonrobotics.junction.AutoLog @AutoLog}
 *      generated files in kotlin classes, not to create them.
 *  </p>
 *  <p>
 *  - When using kapt to generate classes, we have to mark every field of the {@link FlywheelIOInputs}
 *      as @JvmField, which is just annoying and impacts readability.
 *  </p>
 * </p>
 */
public interface FlywheelIO {
    /**
     * Class intended to log all relevant fields that might change during a match.
     * These inputs may be used for:
     *  <p>- Creating alerts (i.e. when a motor disconnects)</p>
     *  <p>- Tune PIDF constants in AdvantageScope (using Mechanical Advantage's LoggedTunableNumber)</p>
     *  <p>- Replay a match in AdvantageScope (using Replay Mode)</p>
     *
     *  All of these fields should be published periodically through
     *  {@link org.littletonrobotics.junction.Logger Logger.processInputs(String, IOInputsAutoLogged)}
     *
     *  <p>
     *      If you are having trouble with your generated {@link FlywheelIOInputsAutoLogged}, it is most probably
     *      because of wrong kapt configuration. Ask an Area Lead or Captain.
     *  </p>
     */
    @AutoLog
    class FlywheelIOInputs {
        /** Flywheel wise fields */
        public MutAngularVelocity flywheelActualVelocity = DegreesPerSecond.mutable(0.0);
        public MutAngularVelocity flywheelTargetVelocity = DegreesPerSecond.mutable(0.0);
        public MutAngularVelocity flywheelPresetVelocity = DegreesPerSecond.mutable(0.0); // If applicable

        /** Lead motor wise fields */
        public boolean isLeadMotorConnected = false;
        public MutAngle leadMotorPosition = Degrees.mutable(0.0);
        public MutAngularVelocity leadMotorVelocity = DegreesPerSecond.mutable(0.0);
        public MutTemperature leadMotorTemperature = Celsius.mutable(0.0);
        public MutVoltage leadMotorOutputVoltage = Volts.mutable(0.0);
        public MutCurrent leadMotorSupplyCurrent = Amps.mutable(0.0);  // Current supplied from battery
        public MutCurrent leadMotorTorqueCurrent = Amps.mutable(0.0);  // Reflects the physical load
        public double leadMotorPower = 0.0;

        /** Follower motor wise fields (duplicate for number of followers, or delete if not applicable) */
        public boolean isFollowerMotorConnected = false;
        public MutAngle followerMotorPosition = Degrees.mutable(0.0);
        public MutAngularVelocity followerMotorVelocity = DegreesPerSecond.mutable(0.0);
        public MutTemperature followerMotorTemperature = Celsius.mutable(0.0);
        public MutVoltage followerMotorOutputVoltage = Volts.mutable(0.0);
        public MutCurrent followerMotorSupplyCurrent = Amps.mutable(0.0);   // Current supplied from battery
        public MutCurrent followerMotorTorqueCurrent = Amps.mutable(0.0);   // Reflects the physical load
        public double followerMotorPower = 0.0;
    }

    /**
     * Intended to update any relevant fields in {@link FlywheelIOInputs}.
     * Might change depending on the implementation (i.e. simulation does not need to check motors' connectivity).
     * @param inputs The generated {@link FlywheelIOInputs} object keeping track of everything.
     */
    void updateFlywheelInputs(FlywheelIOInputs inputs);

    /**
     * Used to update the in-file variable containing the manual target velocity. This resets with every code reload.
     * Testing / Showcase purposes.
     * @param newFlywheelManualVelocity Obtained live through Elastic.
     */
    void updateFlywheelManualVelocity(AngularVelocity newFlywheelManualVelocity);

    /**
     * Used to re-configure the motors with the new PIDF. These gains reset with every code reload.
     * All other settings are obtained through the pre-established motor configurations.
     * @param kP Obtained live through Elastic.
     * @param kI Obtained live through Elastic.
     * @param kD Obtained live through Elastic.
     * @param kF Obtained live through Elastic.
     * @param slot Which PIDF gains to update [0, 1]
     */
    void updateFlywheelMotorsPIDF(double kP, double kI, double kD, double kF, int slot);

    /**
     * Same as {@link #updateFlywheelMotorsPIDF(double, double, double, double, int)}, but defaults the
     * parameter 'slot' to 0. Note that the aforementioned method must be implemented.
     * @param kP Obtained live through Elastic.
     * @param kI Obtained live through Elastic.
     * @param kD Obtained live through Elastic.
     * @param kF Obtained live through Elastic.
     */
    default void updateFlywheelMotorsPIDF(double kP, double kI, double kD, double kF) {
        updateFlywheelMotorsPIDF(kP, kI, kD, kF, 0);
    }

    /**
     * Sets the flywheel to the manually set velocity through Elastic. This resets with every code reload.
     * <p>Make sure to update your flywheel target velocity variable for telemetry</p>
     * @see #updateFlywheelManualVelocity(AngularVelocity)
     * @return A {@link Runnable} setting the flywheel manual target velocity
     */
    Runnable enableFlywheelManualVelocity();

    /**
     * Only if applicable.
     * <p>Sets the flywheel to the preset velocity stored in constants.</p>
     * <p>This does not change live, only in-code.</p>
     * <p>Make sure to update your flywheel target velocity variable for telemetry</p>
     * @return A {@link Runnable} setting the flywheel preset target velocity
     */
    Runnable enableFlywheelPresetVelocity();

    /**
     * Only if applicable. For distance based / state based flywheels specifically.
     * <p>Sets the calculated flywheel velocity, i.e. through interpolation / polynomial</p>
     * <p>Calculation is delegated to a separate method</p>
     * <p>Make sure to update your flywheel target velocity variable for telemetry</p>
     * @param flywheelCalculatedVelocity Flywheel velocity; must be calculated beforehand.
     * @return A {@link Runnable} setting the flywheel calculated target velocity
     */
    Runnable enableFlywheelCalculatedVelocity(AngularVelocity flywheelCalculatedVelocity);

    /**
     * Disables the flywheel motors.
     * @return A {@link Runnable} stopping the flywheel
     */
    Runnable stopFlywheel();

    /**
     * Used <b>ONLY</b> for SysId. Sets the voltage to the lead motor -and by consequence the follower motor.
     * The voltage value must be clamped within {@code [-12.0, 12.0]} before giving it to the implementations.
     * @param voltage The desired voltage.
     */
    Runnable setFlywheelSysIdMotorsVoltage(Voltage voltage);

    /**
     * Merely changes the Neutral / Idle mode of the motors to coast for easier manipulation.
     * @return A {@link Runnable} coasting all flywheel motors
     */
    Runnable coastFlywheelMotors();

    /**
     * Merely changes the Neutral / Idle mode of the motors to brake to avoid unintended movement during match.
     * @return A {@link Runnable} braking all flywheel motors
     */
    Runnable brakeFlywheelMotors();

    /**
     * Applies the configuration inside {@link FlywheelConstants.MotorConfiguration}. Follower commands are included.
     */
    void initialMotorConfiguration();
}
