package solverslib.commandbase;

import static solverslib.hardware.Globals.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import solverslib.hardware.Robot;

public class Outtake extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public void init(){
        if(opModeType.equals(OpModeType.TELEOP)){
            robot.rotate_chamber.setPosition(0);
            robot.pinch_chamber.setPosition(0.5);

            robot.right_swing.setPosition(0.07);
            robot.left_swing.setPosition(0.07);
        }

    }

    public void armDown(){
        robot.left_swing.setPosition(OUTTAKE_ARM_DOWN);
        robot.right_swing.setPosition(OUTTAKE_ARM_DOWN);
    }

    public void armUp(){
        robot.left_swing.setPosition(OUTTAKE_ARM_HOVER);
        robot.right_swing.setPosition(OUTTAKE_ARM_HOVER);
    }

    public void armScore(){
        robot.left_swing.setPosition(OUTTAKE_ARM_SCORE);
        robot.right_swing.setPosition(OUTTAKE_ARM_SCORE);
    }

    public void closeClaw(){
        robot.pinch_chamber.setPosition(OUTTAKE_CLOSE);
    }

    public void openClaw(){
        robot.pinch_chamber.setPosition(OUTTAKE_OPEN);
    }

    public void rotateClaw(double position){
        robot.rotate_chamber.setPosition(position);
    }

    @Override
    public void periodic(){

    }
}
