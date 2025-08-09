package solverslib.commandbase.commands;

import static solverslib.hardware.Globals.OUTTAKE_NOTROTATED;
import static solverslib.hardware.Globals.OUTTAKE_ROTATED;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import solverslib.hardware.Robot;

public class ScoreSpecimen extends SequentialCommandGroup {
    public ScoreSpecimen(Robot robot){
        addCommands(
                new InstantCommand(() -> robot.outtake.armScore()),
                new WaitCommand(300),
                new ParallelCommandGroup(
                        new InstantCommand(() -> robot.outtake.openClaw()),
                        new InstantCommand(() -> robot.outtake.armDown())
                ),
                new WaitCommand(100),
                new InstantCommand(() -> robot.outtake.rotateClaw(OUTTAKE_NOTROTATED))
        );
    }
}
