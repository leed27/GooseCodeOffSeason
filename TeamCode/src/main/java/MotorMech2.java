import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class MotorMech2 {
    public DcMotorEx left_horizontal, right_horizontal, hang;
    public int targetPosition;

    public double power = 0;

    public MotorMech2(@NonNull HardwareMap hardwareMap, double power, boolean craneByPower){
        left_horizontal = hardwareMap.get(DcMotorEx.class, "left_horizontal");
        left_horizontal.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        right_horizontal = hardwareMap.get(DcMotorEx.class, "right_horizontal");
        right_horizontal.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        right_horizontal.setDirection(DcMotorEx.Direction.REVERSE);
        left_horizontal.setDirection(DcMotorEx.Direction.FORWARD);
        resetEncoders();

        hang = hardwareMap.get(DcMotorEx.class, "hang");
        hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hang.setTargetPosition(0);
        hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hang.setDirection(DcMotorEx.Direction.REVERSE);


        targetPosition = 0;

        setPower(power);
        this.power = power;
        setTargetPosition(0);
        setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public void setTargetPosition(int target){
        setPower(0.8);
        left_horizontal.setTargetPosition(target);
        right_horizontal.setTargetPosition(target);
        targetPosition = target;
    }

    public void setTargetPosition(int target, double power){
        setPower(power);
        left_horizontal.setTargetPosition(target);
        right_horizontal.setTargetPosition(target);
        targetPosition = target;
    }

    public void resetEncoders(){
        left_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        right_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        setTargetPosition(0);
        setPower(power);

        left_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        right_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public void craneMaintenance(){
        if(left_horizontal.getCurrentPosition() < 50 && left_horizontal.getCurrent(CurrentUnit.AMPS) > 0.5 && left_horizontal.getTargetPosition() == 0){
            left_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            left_horizontal.setTargetPosition(0);
            left_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            left_horizontal.setPower(0);
        }
        if(right_horizontal.getCurrentPosition() < 50 && right_horizontal.getCurrent(CurrentUnit.AMPS)  > 0.5 && right_horizontal.getTargetPosition() == 0){
            right_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            right_horizontal.setTargetPosition(0);
            right_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            right_horizontal.setPower(0);
        }
    }

    public void move(double movement, boolean byPower){
        if(movement > 0 && byPower){
            setTargetPosition(3600, movement);
        }else if(movement < 0 && byPower){
            setTargetPosition(0, -movement);
        }else if(byPower){
            setPower(0);
        }else if(movement > 3600){
            setTargetPosition(3600);
        }else if(movement < 0){
            setTargetPosition(0);
        }else{
            setTargetPosition((int)movement);
        }
    }
    public void setPower(double power){
        left_horizontal.setPower(power);
        right_horizontal.setPower(power);
    }

    public int getCurrentLeftPosition() { return left_horizontal.getCurrentPosition(); }

    public int getCurrentRightPosition() { return right_horizontal.getCurrentPosition(); }

    public boolean offCheck(){ return getCurrentLeftPosition() < 0 || getCurrentRightPosition() < 0; }


    public void setMode(DcMotorEx.RunMode mode){
        left_horizontal.setMode(mode);
        right_horizontal.setMode(mode);
    }


    public double getAmpsLeft(){return left_horizontal.getCurrent(CurrentUnit.AMPS);}
    public double getAmpsRight(){return right_horizontal.getCurrent(CurrentUnit.AMPS);}

    public void setHang(){
        movevertically(hang, 1800, 1);
    }


    public void movevertically(DcMotorEx lipsey, int position, double power) {
        untoPosition(lipsey);
        runtoPosition(lipsey);
        lipsey.setTargetPosition(position);
        lipsey.setPower(power);
    }

    public void runtoPosition(DcMotorEx John) {
        John.setTargetPosition(0);
        John.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        John.setPower(0);
    }
    public void untoPosition(DcMotorEx Neil) {
        Neil.setPower(0);
        Neil.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }
}