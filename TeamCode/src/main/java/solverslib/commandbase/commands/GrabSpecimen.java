package solverslib.commandbase.commands;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import static solverslib.hardware.Globals.*;
import solverslib.hardware.Robot;
import solverslib.commandbase.Outtake;

public class GrabSpecimen extends SequentialCommandGroup {
    public GrabSpecimen(Robot robot){
        addCommands(
                new InstantCommand(() -> robot.outtake.closeClaw()),
                new WaitCommand(150),
                new InstantCommand(() -> robot.outtake.armUp()),
                new WaitCommand(200),
                new InstantCommand(() -> robot.outtake.rotateClaw(OUTTAKE_ROTATED))
        );
    }
}
