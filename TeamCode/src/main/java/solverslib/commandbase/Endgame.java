package solverslib.commandbase;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;

import solverslib.hardware.Robot;

public class Endgame extends SubsystemBase {
    private final Robot robot = Robot.getInstance();
    private static final PIDFController slidePIDF = new PIDFController(0.007,0, 0.00017, 0.00023);
    public double target;

    public boolean slidesReached;

    //true is retracted, false is extended
    public boolean slidesRetracted;



    public void balance(){
        robot.right_swing.setPosition(0.2);
        robot.left_swing.setPosition(0.2);
        robot.intake.setSlideTarget(500);
    }
    public void setSlideTarget(double target){
        //FAILSAFE IF WE DON'T WANT SLIDES TO GO OUT OF ITS RANGE ---> this.target = Range.clip(target, 0, MAX_SLIDES_EXTENSION);
        slidePIDF.setSetPoint(target);
    }

    public void autoUpdateSlides(){
        double power = slidePIDF.calculate(robot.left_hang.getPosition(), target);
        slidesReached = slidePIDF.atSetPoint();
        slidesRetracted = (target <= 0) && slidesReached;

        if(target == 0 && !slidesReached){
            power -= 0.1;
        }

        if(slidesRetracted){
            robot.left_hang.setPower(0);
            robot.right_hang.setPower(0);
        }else{
            robot.left_hang.setPower(power);
            robot.right_hang.setPower(power);
        }
    }

    //periodic runs in a loop
    @Override
    public void periodic(){
        autoUpdateSlides();
    }
}
