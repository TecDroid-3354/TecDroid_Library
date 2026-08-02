package frc.tecdroid3354.core

import com.pathplanner.lib.auto.AutoBuilder
import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.units.Units.Degrees
import edu.wpi.first.units.measure.Angle
import edu.wpi.first.units.measure.MutAngle
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.button.Trigger
import frc.tecdroid3354.RobotVisualizer
import frc.tecdroid3354.commands.DriveCommands
import frc.tecdroid3354.constants.RobotConstants
import frc.tecdroid3354.constants.RobotMode
import frc.tecdroid3354.generated.SwerveTunerConstants
import frc.tecdroid3354.subsystems.angularPosition.JointIO
import frc.tecdroid3354.subsystems.angularPosition.JointIOSim
import frc.tecdroid3354.subsystems.angularPosition.JointIOTalonFX
import frc.tecdroid3354.subsystems.angularPosition.JointSubsystem
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIO
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIOSim
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelIOTalonFX
import frc.tecdroid3354.subsystems.angularVelocity.FlywheelSubsystem
import frc.tecdroid3354.subsystems.drive.*
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIO
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIOSim
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorIOTalonFX
import frc.tecdroid3354.subsystems.linearDisplacement.ElevatorSubsystem
import frc.tecdroid3354.subsystems.vision.*
import frc.tecdroid3354.utils.InstantCommand
import frc.tecdroid3354.utils.meters
import frc.tecdroid3354.utils.radians
import org.ironmaple.simulation.SimulatedArena
import org.ironmaple.simulation.drivesims.SwerveDriveSimulation
import org.littletonrobotics.junction.Logger
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser
import kotlin.math.abs
import kotlin.math.atan2

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
    private val lastDriveAngle: MutAngle = Degrees.mutable(0.0)

    private lateinit var autoChooser: LoggedDashboardChooser<Command>

    private lateinit var drive                  : Drive
    private lateinit var mapleSimDrive          : SwerveDriveSimulation
    private lateinit var vision                 : Vision

    private lateinit var jointSubsystem         : JointSubsystem
    private lateinit var elevatorSubsystem      : ElevatorSubsystem
    private lateinit var flywheelSubsystem      : FlywheelSubsystem

    lateinit var robotVisualizer: RobotVisualizer
        
    init
    {
        initializeSubsystems() // This method MUST be the first one called, otherwise you'll be accessing null objects
        configureAutonomousCommands()
        configureBindings()
    }

    fun robotEnabledConfig() {

    }

    fun robotDisabledConfig() {

    }

    fun robotTeleopInitConfig() {

    }

    fun getAutonomousCommand(): Command {
        return Commands.none()
    }

    fun resetSimulation() {
        if (RobotConstants.ROBOT_MODE != RobotMode.SIM) return

        drive.resetOdometry(Pose2d(3.0, 3.0, Rotation2d()))
        SimulatedArena.getInstance().resetFieldForAuto()
    }

    fun updateSimulation() {
        if (RobotConstants.ROBOT_MODE != RobotMode.SIM) return

        SimulatedArena.getInstance().simulationPeriodic()
        Logger.recordOutput("FieldSimulation/RobotPosition", mapleSimDrive.simulatedDriveTrainPose)
        Logger.recordOutput(
            "FieldSimulation/Fuel",
            *SimulatedArena.getInstance().getGamePiecesArrayByType("Fuel")
        )
    }

    /**
     * Use this method to define your `trigger->command` mappings. Triggers can be created via the
     * [Trigger] constructor that takes a [BooleanSupplier][java.util.function.BooleanSupplier]
     * with an arbitrary predicate, or via the named factories in [GenericHID][edu.wpi.first.wpilibj2.command.button.CommandGenericHID]
     * subclasses such for [Xbox][CommandXboxController]/[PS4][edu.wpi.first.wpilibj2.command.button.CommandPS4Controller]
     * controllers or [Flight joysticks][edu.wpi.first.wpilibj2.command.button.CommandJoystick].
     */
    private fun configureBindings() { // Would leave better commands in template, but I'm using a keyboard.
        drive.defaultCommand = DriveCommands.joystickDrive(
            drive,
            { MathUtil.applyDeadband(-driverController.leftY, 0.05) },
            { MathUtil.applyDeadband(-driverController.leftX, 0.05) },
            { MathUtil.applyDeadband(-driverController.rightX, 0.05) },
        )

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
            .onTrue(
                InstantCommand({ drive.resetOdometry(
                    Pose2d(3.0, 3.0, Rotation2d())
                ) })
            )

        driverController.x()
            .whileTrue(jointSubsystem.setJointManualPosition().InstantCommand(jointSubsystem))
            .onFalse(jointSubsystem.setJointHomePosition().InstantCommand(jointSubsystem))
    }

    private fun getAngleFromJoystick(): Angle {
        if (abs(-driverController.rightY) < 0.3 && abs(driverController.rightX) < 0.3) return lastDriveAngle

        val atan2Rad = atan2( // Gets the "raw" angle from the joysticks, after applying a dead band
            MathUtil.applyDeadband(
                if (RobotConstants.IS_RED_ALLIANCE.asBoolean) -driverController.rightY else driverController.rightY, 0.3),
            MathUtil.applyDeadband(
                if (RobotConstants.IS_RED_ALLIANCE.asBoolean) driverController.rightX else -driverController.rightX, 0.3)
        ).plus(Math.PI / 2) // Corrects due to joystick's own reference frame

        lastDriveAngle.mut_replace(atan2Rad.radians)

        // To ensure consistent rotation, 180.0 degrees are added when in Blue Alliance.
        return lastDriveAngle
    }

    /**
     * Adds every PathPlanner / Choreo auto to the auto chooser
     */
    private fun configureAutonomousCommands() {
        autoChooser.addDefaultOption("None", Commands.none())
    }

    /**
     * Initializes all subsystems with the corresponding IOLayer, depending on [RobotConstants.ROBOT_MODE]
     */
    private fun initializeSubsystems() {
        when(RobotConstants.ROBOT_MODE) {
            RobotMode.REAL -> {
                drive = Drive(
                    GyroIOPigeon2(),
                    ModuleIOTalonFX(SwerveTunerConstants.FrontLeft), ModuleIOTalonFX(SwerveTunerConstants.FrontRight),
                    ModuleIOTalonFX(SwerveTunerConstants.BackLeft), ModuleIOTalonFX(SwerveTunerConstants.BackRight),
                    {}
                )
                vision = Vision(
                    drive,
                    VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation),
                    VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation),
                    VisionIOLimelight(VisionConstants.camera2Name, drive::getRotation),
                )

                jointSubsystem = JointSubsystem(JointIOTalonFX())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIOTalonFX())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIOTalonFX())
            }
            RobotMode.SIM -> {
                mapleSimDrive = SwerveDriveSimulation(
                    Drive.getMapleSimConfig(),
                    Pose2d(3.0.meters, 3.0.meters, Rotation2d())
                )
                SimulatedArena.getInstance().addDriveTrainSimulation(mapleSimDrive)

                drive = Drive(
                    GyroIOSim(mapleSimDrive.gyroSimulation),
                    ModuleIOSim(mapleSimDrive.modules[0]), ModuleIOSim(mapleSimDrive.modules[1]),
                    ModuleIOSim(mapleSimDrive.modules[2]), ModuleIOSim(mapleSimDrive.modules[3]),
                    mapleSimDrive::setSimulationWorldPose
                )
                vision = Vision(
                    drive,
                    VisionIOPhotonVisionSim(VisionConstants.camera0Name, VisionConstants.robotToCamera0, mapleSimDrive::getSimulatedDriveTrainPose),
                    VisionIOPhotonVisionSim(VisionConstants.camera1Name, VisionConstants.robotToCamera1, mapleSimDrive::getSimulatedDriveTrainPose),
                    VisionIOPhotonVisionSim(VisionConstants.camera2Name, VisionConstants.robotToCamera2, mapleSimDrive::getSimulatedDriveTrainPose),
                )

                jointSubsystem = JointSubsystem(JointIOSim())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIOSim())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIOSim())
            }
            RobotMode.REPLAY -> {
                drive = Drive(
                    object : GyroIO {},
                    object : ModuleIO {}, object : ModuleIO {}, object : ModuleIO {}, object : ModuleIO {},
                    {}
                )
                // One dummy IO for each camera on the robot
                vision = Vision(drive, object : VisionIO {}, object : VisionIO {}, object : VisionIO {})

                jointSubsystem = JointSubsystem(JointIO.DummyJointIO())
                elevatorSubsystem = ElevatorSubsystem(ElevatorIO.DummyElevatorIO())
                flywheelSubsystem = FlywheelSubsystem(FlywheelIO.DummyFlywheelIO())
            }
        }

        // Initializes auto chooser after AutoBuilder was configured inside Drive.
        autoChooser = LoggedDashboardChooser("Auto Choices", AutoBuilder.buildAutoChooser())

        robotVisualizer = RobotVisualizer( // Independent of robot mode. Initialized at last to give it the parameters.
            { jointSubsystem.getJointPosition() },
            { elevatorSubsystem.getElevatorDisplacement() }
        )
    }
}