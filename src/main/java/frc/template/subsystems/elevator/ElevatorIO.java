package frc.template.subsystems.elevator;

import edu.wpi.first.units.measure.*;
import org.littletonrobotics.junction.AutoLog;

import static edu.wpi.first.units.Units.*;

/**
 * I/O (Input/Output) interface intended for an elevator controlled by two motor controllers set as lead and follower.
 * In case this motor setup changes for the current season, just add / remove the motors in both this interface
 * and any implementation (i.e. {@link ElevatorIOTalonFX}) inside YOUR robot project. Any changes to this or any
 * template present in the repository must be discussed with the lead(s) of the area.
 * <p>
 * Why is this file in Java if our primary language is Kotlin? <p>
 *  - Java annotations (i.e. {@link org.littletonrobotics.junction.AutoLog @AutoLog}) are not present in kotlin.
 *      This is solvable using kapt, yet having this sort of bridge can make builds / deploys take significantly longer.
 *      Note that kapt is still being used, yet only to read the {@link org.littletonrobotics.junction.AutoLog @AutoLog}
 *      generated files in kotlin classes, not to create them.<p>
 *  - When using kapt to generate classes, we have to mark every field of the {@link ElevatorIOInputs ElevatorIOInputs}
 *      as @JvmField, which is just annoying and impacts readability.
 */
public interface ElevatorIO {
    /**
     * Class intended to log all relevant fields that might change during a match.
     * These inputs may be used for:
     *  - Creating alerts (i.e. when a motor disconnects)
     *  - Tune PIDF constants in AdvantageScope (using Mechanical Advantage's LoggedTunableNumber)
     *  - Replay a match in AdvantageScope (using Replay Mode)
     */
    @AutoLog
    class ElevatorIOInputs {
        /** Elevator wise fields */
        public MutDistance elevatorDisplacement = Meters.mutable(0.0);          // Actual Displacement
        public MutDistance elevatorTargetDisplacement = Meters.mutable(0.0);    // Setpoint / Target

        /** Lead motor wise fields */
        public boolean isLeadMotorConnected = false;                                // signalsRespond ? true : false
        public MutAngle leadMotorPosition = Degrees.mutable(0.0);       // Obtained after transforming
                                                                                    // elevatorDisplacement accounting
                                                                                    // for sprocket and reduction.

        public MutAngle leadMotorTargetPosition = Degrees.mutable(0.0); // Obtained after transforming
                                                                                    // elevatorTargetDisplacement
                                                                                    // accounting for sprocket and
                                                                                    // reduction.
        public MutAngularVelocity leadMotorVelocity = DegreesPerSecond.mutable(0.0);
        public MutVoltage leadMotorOutputVoltage = Volts.mutable(0.0);     // Output voltage of the motor
        public MutCurrent leadMotorSupplyCurrent = Amps.mutable(0.0);   // Current supplied to the motor

        /** Follower motor wise fields */
        public boolean isFollowerMotorConnected = false;                             // signalsRespond ? true : false
        public MutVoltage followerMotorOutputVoltage = Volts.mutable(0.0);  // Output voltage of the motor
        public MutCurrent followerMotorSupplyCurrent = Amps.mutable(0.0);   // Current supplied to the motor
    }

    /**
     * Intended to update any relevant fields in {@link ElevatorIOInputs ElevatorIOInputs}.
     * Might change depending on the implementation (i.e. simulation does not need to check motors' connectivity).
     * @param inputs The generated {@link ElevatorIOInputsAutoLogged} object keeping track of everything.
     */
    void updateInputs(ElevatorIOInputs inputs);

    /**
     * Used for SysId characterization. Sets the voltage to the lead motor -and by consequence the follower motor.
     * The voltage value must be clamped within {@code [-12.0, 12.0]} before giving it to the implementations.
     * @param voltage The desired voltage.
     */
    void setElevatorMotorsVoltage(Voltage voltage);

    /**
     * Changes the target displacement of the elevator.
     * @param elevatorTargetDisplacement The desired displacement for the elevator.
     */
    void setElevatorTargetDisplacement(Distance elevatorTargetDisplacement);

    /**
     * Sets the motors' voltage to 0.
     * Might change depending on the implementation, yet the default body of the function calls
     * {@link #setElevatorMotorsVoltage(Voltage) setElevatorMotorsVoltage(Volts.of(0.0))}
     */
    default void stopElevator() { setElevatorMotorsVoltage(Volts.of(0.0)); }

    /**
     * Uses the lead motor position as reference.
     * @return an {@link Angle} containing the lead motor position.
     */
    Angle getElevatorMotorPosition();

    /**
     * Uses the lead motor position as reference.
     * @return an {@link AngularVelocity} containing the lead motor velocity.
     */
    AngularVelocity getElevatorMotorVelocity();

    /**
     * Uses the lead motor position as reference.
     * Power in this context refers to a normalized factor representing the speed of the motor.
     * @return a value within {@code [-1.0, 1.0]} representing the power of the motor.
     */
    Double getElevatorMotorPower();

    /**
     * Merely changes the Neutral / Idle mode of the motors to coast for easier manipulation.
     */
    void coastElevatorMotors();

    /**
     * Merely changes the Neutral / Idle mode of the motors to brake to avoid unintended movement during match.
     */
    void brakeElevatorMotors();
}
