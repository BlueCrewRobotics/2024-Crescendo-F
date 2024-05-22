// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.SignalLogger;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.lib.bluecrew.util.FieldState;
import frc.lib.bluecrew.util.RobotState;
import frc.robot.subsystems.VisionModule;

import static frc.robot.Constants.FieldCoordinates.BLUE_SPEAKER;
import static frc.robot.Constants.FieldCoordinates.RED_SPEAKER;
import static frc.robot.Constants.PhotonVision.ROBOT_TO_TAG_FRONT_RIGHT_CAM_POS;
import static frc.robot.Constants.PhotonVision.ROBOT_TO_TAG_REAR_LEFT_CAM_POS;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

    private Command m_autonomousCommand;
    private Command keepShooterRunning;

    private RobotContainer m_robotContainer;

    private boolean shouldUpdateAutoCommand = false;

    private long startTime;

    private final GenericEntry cameraToggle = Shuffleboard.getTab("Autonomous")
            .add("Camera Toggle", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
        // autonomous chooser on the dashboard.
        m_robotContainer = new RobotContainer();

        startTime = System.nanoTime();

        DataLogManager.start();

        shouldUpdateAutoCommand = false;
        //URCL.start();
        //SignalLogger.start();
    }

    /**
     * This function is called every robot packet, no matter the mode. Use this for items like
     * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before LiveWindow and
     * SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run();

        SmartDashboard.putBoolean("On Red Alliance", FieldState.getInstance().onRedAlliance());
    }

    /**
     * This function is called once each time the robot enters Disabled mode.
     */
    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
        var alliance = DriverStation.getAlliance();
        boolean onRedAlliance = alliance.filter(value -> value == DriverStation.Alliance.Red).isPresent();
        FieldState.getInstance().setOnRedAlliance(onRedAlliance);

        if (cameraToggle.getBoolean(false)) {
            VisionModule.getInstance().setPhotonEstimatorFrontRight(ROBOT_TO_TAG_REAR_LEFT_CAM_POS);
            VisionModule.getInstance().setPhotonEstimatorRearLeft(ROBOT_TO_TAG_FRONT_RIGHT_CAM_POS);
        } else {
            VisionModule.getInstance().setPhotonEstimatorFrontRight(ROBOT_TO_TAG_FRONT_RIGHT_CAM_POS);
            VisionModule.getInstance().setPhotonEstimatorRearLeft(ROBOT_TO_TAG_REAR_LEFT_CAM_POS);
        }

        if (onRedAlliance) {
            FieldState.getInstance().setSpeakerCoords(RED_SPEAKER);
            FieldState.getInstance().setActualSpeakerCoords(RED_SPEAKER);
        } else {
            FieldState.getInstance().setSpeakerCoords(BLUE_SPEAKER);
            FieldState.getInstance().setActualSpeakerCoords(BLUE_SPEAKER);
        }

        if (!shouldUpdateAutoCommand && System.nanoTime() > (startTime + 2E9)) {
            shouldUpdateAutoCommand = true;
        }

        if (shouldUpdateAutoCommand && m_robotContainer.autoOptionsHaveChanged()) {
            m_robotContainer.regenerateAutoCommand();
        }
    }

    /**
     * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
     */
    @Override
    public void autonomousInit() {
        RobotState.getInstance().setIsAutonomous(true);
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        keepShooterRunning = new RunCommand(() -> m_robotContainer.getNotePlayerSubsystem().spinUpShooterForSpeaker())
                .finallyDo(() -> m_robotContainer.getNotePlayerSubsystem().getShooter().stop())
                .withName("KeepShooterRunning");

        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
//            keepShooterRunning.schedule();
        }
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
        RobotState.getInstance().setIsAutonomous(false);
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
//            keepShooterRunning.cancel();
            m_robotContainer.getNotePlayerSubsystem().getShooter().stop();
            m_robotContainer.getSwerveDrive().setShouldUseVision(true);
        }
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testInit() {
        // Cancels all running commands at the start of test mode.
        CommandScheduler.getInstance().cancelAll();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
    }
}
