package solverslib.commandbase;

import static solverslib.hardware.Globals.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;

import solverslib.hardware.Robot;

public class Intake extends SubsystemBase {
    private final Robot robot = Robot.getInstance();
    private static final PIDFController slidePIDF = new PIDFController(0.007,0, 0.00017, 0.00023);

    //true is open, false is closed
    public boolean clawOpen;

    //true is down, false is up
    public boolean intakeDown;

    public double target;

    public boolean slidesReached;

    //true is retracted, false is extended
    public boolean slidesRetracted;

    public void init(){
        slidePIDF.setTolerance(12, 10);
        setSlideTarget(0);

        if(opModeType.equals(OpModeType.AUTO)){
            setClawOpen(false);
        }
    }

    public void setSlideTarget(double target){
        //FAILSAFE IF WE DON'T WANT SLIDES TO GO OUT OF ITS RANGE ---> this.target = Range.clip(target, 0, MAX_SLIDES_EXTENSION);
        slidePIDF.setSetPoint(target);
    }

    public void autoUpdateSlides(){
        double power = slidePIDF.calculate(robot.left_horizontal.getPosition(), target);
        slidesReached = slidePIDF.atSetPoint();
        slidesRetracted = (target <= 0) && slidesReached;

        if(target == 0 && !slidesReached){
            power -= 0.1;
        }

        if(slidesRetracted){
            robot.left_horizontal.setPower(0);
            robot.right_horizontal.setPower(0);
        }else{
            robot.left_horizontal.setPower(power);
            robot.right_horizontal.setPower(power);
        }
    }

    //true if we want it to open, false if we want it to close
    public void setClawOpen(boolean open) {
        if(open){
            robot.pinch_floor.setPosition(INTAKE_OPEN);
        }else{
            robot.pinch_floor.setPosition(INTAKE_CLOSE);
        }

        this.clawOpen = open;
    }

    public void clawDown(boolean down){
        if(down){
            robot.flip_floor.setPosition(INTAKE_DOWN);
        }else{
            robot.flip_floor.setPosition(INTAKE_UP);
        }

        this.intakeDown = down;
    }

    public void rotateClaw(double position){
        robot.rotate_floor.setPosition(position);
    }

    //periodic runs in a loop
    @Override
    public void periodic(){
        autoUpdateSlides();
    }
}
