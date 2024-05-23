package frc.robot.autos;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import frc.lib.bluecrew.util.RobotState;
import frc.robot.commands.IntakeNote;
import frc.robot.subsystems.noteplayer.NotePlayerSubsystem;
import frc.robot.subsystems.swervedrive.SwerveDrive;

public class AutoGrabFromStart extends ParallelRaceGroup {

    public AutoGrabFromStart(int noteToGet, String lastScoredIn, String autoLane, NotePlayerSubsystem notePlayerSubsystem, SwerveDrive swerveDrive) {
        // Follow the path from where we last scored to the starting note we want to get
        // until we get to the end of the path, or we pick up a note
        addCommands(
                AutoBuilder.followPath(PathPlannerPath.fromPathFile(lastScoredIn + "-" + autoLane + "-SN" + noteToGet))
                        .alongWith(notePlayerSubsystem.intakeNote()),
                //Commands.print("Following: " + lastScoredIn + "-" + autoLane + "-SN" + noteToGet),
                new AutoFindAndGoToNote(notePlayerSubsystem, swerveDrive).until(notePlayerSubsystem.getIntake()::noteInIntake)
                        .alongWith(Commands.waitUntil(RobotState.getInstance()::isNoteIsAvailable).andThen(notePlayerSubsystem.intakeNote()))

        );
    }
}
