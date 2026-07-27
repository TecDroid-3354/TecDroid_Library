package frc.tecdroid3354.core

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.Trigger
import frc.tecdroid3354.RobotVisualizer
import frc.tecdroid3354.constants.RobotConstants
import frc.tecdroid3354.constants.RobotMode
import frc.tecdroid3354.subsystems.angularPosition.JointIO
import frc.tecdroid3354.subsystems.angularPosition.JointIOSim
import frc.tecdroid3354.subsystems.angularPosition.JointIOTalonFX
import frc.tecdroid3354.subsystems.angularPosition.JointSubsystem
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIO
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIOSim
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIOTalonFX
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelSubsystem
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIO
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIOSim
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIOTalonFX
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorSubsystem
import frc.tecdroid3354.utils.InstantCommand

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the [Robot]
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 *
 * In Kotlin, it is recommended that all your Subsystems are Kotlin objects. As such, there
 * can only ever be a single instance. This eliminates the need to create reference variables
 * to the various subsystems in this container to pass into to commands. The commands can just
 * directly reference the (single instance of the) object.
 */
object RobotContainer
{
    // Replace with CommandPS4Controller or CommandJoystick if needed
    private val driverController = CommandXboxController(RobotConstants.MAIN_CONTROLLER_PORT)

    private lateinit var jointSubsystem: JointSubsystem
    private lateinit var elevatorSubsystem: ElevatorSubsystem
    private lateinit var flywheelSubsystem: FlywheelSubsystem

    lateinit var robotVisualizer: RobotVisualizer
        
    init
    {
        initializeSubsystems() // This method MUST be the first one called, otherwise you'll be accessing null objects
        configureBindings()
    }

    fun robotEnabledConfig() {

    }

    fun robotDisabledConfig() {

    }

    fun robotTeleopInitConfig() {

    }

    fun robotAutoInitConfig() {

    }

    fun getAutonomousCommand(): Command {
        return Commands.none()
    }

    /**
     * Use this method to define your `trigger->command` mappings. Triggers can be created via the
     * [Trigger] constructor that takes a [BooleanSupplier][java.util.function.BooleanSupplier]
     * with an arbitrary predicate, or via the named factories in [GenericHID][edu.wpi.first.wpilibj2.command.button.CommandGenericHID]
     * subclasses such for [Xbox][CommandXboxController]/[PS4][edu.wpi.first.wpilibj2.command.button.CommandPS4Controller]
     * controllers or [Flight joysticks][edu.wpi.first.wpilibj2.command.button.CommandJoystick].
     */
    private fun configureBindings() { // Would leave better commands in template, but I'm using a keyboard.
        driverController.a()
            .whileTrue(elevatorSubsystem.setElevatorIdleDisplacement().InstantCommand(elevatorSubsystem))
            .onFalse(elevatorSubsystem.setElevatorHomeDisplacement().InstantCommand(elevatorSubsystem))

        driverController.y()
            .whileTrue(
                SequentialCommandGroup(
                    jointSubsystem.setJointManualPosition().InstantCommand(jointSubsystem),
                    elevatorSubsystem.setElevatorManualTargetDisplacement().InstantCommand(elevatorSubsystem)
                )
            )
            .onFalse(
                SequentialCommandGroup(
                    elevatorSubsystem.setElevatorHomeDisplacement().InstantCommand(elevatorSubsystem),
                    jointSubsystem.setJointHomePosition().InstantCommand(jointSubsystem)
                )
            )

        driverController.b()
            .whileTrue(jointSubsystem.setJointIdlePosition().InstantCommand(jointSubsystem))

        driverController.x()
            .whileTrue(jointSubsystem.setJointManualPosition().InstantCommand(jointSubsystem))
            .onFalse(jointSubsystem.setJointHomePosition().InstantCommand(jointSubsystem))
    }

    /**
     * Initializes all subsystems with the corresponding IOLayer, depending on [RobotConstants.ROBOT_MODE]
     */
    private fun initializeSubsystems() {
        when(RobotConstants.ROBOT_MODE) {
            RobotMode.REAL -> {
                jointSubsystem = JointSubsystem(JointIOTalonFX())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIOTalonFX())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIOTalonFX())
            }
            RobotMode.SIM -> {
                jointSubsystem = JointSubsystem(JointIOSim())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIOSim())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIOSim())
            }
            RobotMode.REPLAY -> {
                jointSubsystem = JointSubsystem(JointIO.DummyJointIO())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIO.DummyElevatorIO())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIO.DummyFlywheelIO())
            }
        }

        robotVisualizer = RobotVisualizer( // Independent of robot mode. Initialized at last to give it the parameters.
            { jointSubsystem.getJointPosition() },
            { elevatorSubsystem.getElevatorDisplacement() }
        )
    }
}