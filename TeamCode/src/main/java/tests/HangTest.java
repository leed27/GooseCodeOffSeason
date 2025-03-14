package tests;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp(name= "HangTest", group="Linear Opmode")
public class HangTest extends LinearOpMode {

    // Declare OpMode members.
    private DcMotorEx.ZeroPowerBehavior brake = DcMotorEx.ZeroPowerBehavior.BRAKE;
    private DcMotorEx.ZeroPowerBehavior floatt = DcMotorEx.ZeroPowerBehavior.FLOAT;

    private DcMotorEx leftFront, leftRear, rightRear, rightFront; //wheels

    private DcMotorEx right_horizontal,left_horizontal; //slides

    private DcMotorEx right_hang, left_hang;

    private ServoImplEx rotate_floor, pinch_floor, flip_floor, right_swing, left_swing, rotate_chamber, pinch_chamber;

    ElapsedTime drawerTimer = new ElapsedTime();
    ElapsedTime servoTimer = new ElapsedTime();

    boolean motorState = true;

    int errorBound = 60;
    boolean holding = false;

    int h = 0;

    public enum state {
        DRIVE_FORWARD,
        DRIVE_BACK
    };

    state driveState = state.DRIVE_FORWARD;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        drawerTimer.reset();

        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");

        right_horizontal = hardwareMap.get(DcMotorEx.class, "right_horizontal");
        left_horizontal = hardwareMap.get(DcMotorEx.class, "left_horizontal");

        rotate_floor = hardwareMap.get(ServoImplEx.class, "rotate_floor");
        pinch_floor = hardwareMap.get(ServoImplEx.class, "pinch_floor");
        flip_floor = hardwareMap.get(ServoImplEx.class, "flip_floor");

        right_swing = hardwareMap.get(ServoImplEx.class, "right_swing");
        left_swing = hardwareMap.get(ServoImplEx.class, "left_swing");

        rotate_chamber = hardwareMap.get(ServoImplEx.class, "rotate_chamber");
        pinch_chamber = hardwareMap.get(ServoImplEx.class, "pinch_chamber");

        right_hang = hardwareMap.get(DcMotorEx.class, "right_hang");
        left_hang = hardwareMap.get(DcMotorEx.class, "left_hang");

        telemetry.update();

        rightFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightRear.setDirection(DcMotorEx.Direction.REVERSE);
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);

        right_horizontal.setDirection(DcMotorEx.Direction.FORWARD);
        left_horizontal.setDirection(DcMotorEx.Direction.REVERSE);

        right_horizontal.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left_horizontal.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        right_hang.setDirection(DcMotorEx.Direction.REVERSE);
        left_hang.setDirection(DcMotorEx.Direction.FORWARD);

        right_hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left_hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        reset();
        resetHang();

        servoTimer.reset();
        telemetry.update();

        driveState = state.DRIVE_FORWARD;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if(opModeInInit()){
        }

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("right_hang: ", right_hang.getCurrentPosition());
                telemetry.addData("left_hang: ", left_hang.getCurrentPosition());
                telemetry.addData("built", "yay");
                //telemetry.addData("mode: ", right_horizontal.getMode());
                //telemetry.addData("motor state: ", motorState);


                telemetry.update();

                if (gamepad2.triangle) {
                   movevertically(right_hang, 2800, 1);
                   movevertically(left_hang, 2800, 1);
                    //moveHang(4250, false);
                } else if (gamepad2.circle) {
                    movevertically(right_hang, 380, 1);
                    movevertically(left_hang, 380, 1);
                    //moveHang(400, false);
                } else if (gamepad2.cross) {
                    movevertically(right_hang, 3550, 1);
                   movevertically(left_hang, 3550, 1);
                    //moveHang(6300, false); //6300
                }

                if(gamepad2.right_bumper){
                    movevertically(right_horizontal, 500, 0.2);
                    movevertically(left_horizontal, 500, 0.2);
                }
                else if(gamepad2.left_bumper){
                    movevertically(right_horizontal, 0, 0.2);
                    movevertically(left_horizontal, 0, 0.2);
                }
//                else if (gamepad2.square) {
   //             movevertically(right_hang, 1900, 1);
   //                 movevertically(left_hang, 1900, 1);
//                    moveHang(1900, false);
//                }
                else if (gamepad2.left_trigger > 0 || gamepad2.right_trigger > 0) {
                   //moveHang(gamepad2.left_trigger - gamepad2.right_trigger, true);
                }

//                if(gamepad2.right_trigger > 0){
//                    right_hang.setPower(1);
//                    left_hang.setPower(1);
//                }
//                else if(gamepad2.left_trigger > 0){
//                    right_hang.setPower(-1);
//                    left_hang.setPower(-1);
//                }
//                else{
//                    right_hang.setPower(0);
//                    left_hang.setPower(0);
//                }


            }

        }
    }

    public void reset(){
        right_horizontal.setPower(0);
        left_horizontal.setPower(0);

        right_horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        right_horizontal.setTargetPosition(0);
        left_horizontal.setTargetPosition(0);

        right_horizontal.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_horizontal.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void resetHang(){
        right_hang.setPower(0);
        left_hang.setPower(0);

        right_hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        right_hang.setTargetPosition(0);
        left_hang.setTargetPosition(0);

        right_hang.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left_hang.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void waitforDrawer(DcMotor george) {
        while(!(george.getCurrentPosition() > george.getTargetPosition() - errorBound && george.getCurrentPosition() < george.getTargetPosition() + errorBound));
    }

    public boolean waitforDrawers(DcMotor george, DcMotor BobbyLocks) {
        return ((george.getCurrentPosition() > george.getTargetPosition() - errorBound && george.getCurrentPosition() < george.getTargetPosition() + errorBound) &&
                (BobbyLocks.getCurrentPosition() > BobbyLocks.getTargetPosition() - errorBound && BobbyLocks.getCurrentPosition() < BobbyLocks.getTargetPosition() + errorBound));
    }

    public boolean drawersDone(DcMotor george, DcMotor BobbyLocks) {
        return ((george.getCurrentPosition() > george.getTargetPosition() - errorBound && george.getCurrentPosition() < george.getTargetPosition() + errorBound) &&
                (BobbyLocks.getCurrentPosition() > BobbyLocks.getTargetPosition() - errorBound && BobbyLocks.getCurrentPosition() < BobbyLocks.getTargetPosition() + errorBound));
    }

    public void nostall(DcMotorEx Harry) {
        Harry.setZeroPowerBehavior(floatt);
        Harry.setPower(0);
    }

    public void stall(DcMotorEx DcMotar) {
        DcMotar.setZeroPowerBehavior(brake);
        DcMotar.setPower(0);
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

    public void settle_slides(){
        if(left_horizontal.getCurrentPosition() < 25 && left_horizontal.getCurrentAlert(CurrentUnit.AMPS) > 0.5 && left_horizontal.getTargetPosition() == 0){
            left_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            left_horizontal.setTargetPosition(0);
            left_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            left_horizontal.setPower(0);

        }
        if(right_horizontal.getCurrentPosition() < 25 && right_horizontal.getCurrentAlert(CurrentUnit.AMPS) > 0.5 && left_horizontal.getTargetPosition() == 0){
            right_horizontal.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            left_horizontal.setTargetPosition(0);
            right_horizontal.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            right_horizontal.setPower(0);
        }
    }


    public void moveHang(double movement, boolean byPower){
        holding = false;
        right_hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(movement > 0 && byPower){
            left_hang.setPower(movement);
            right_hang.setPower(movement);
            left_hang.setTargetPosition(6500);
            right_hang.setTargetPosition(6500);
        }else if(movement < 0 && byPower){
            left_hang.setPower(-movement);
            right_hang.setPower(-movement);
            left_hang.setTargetPosition(0);
            right_hang.setTargetPosition(0);
        }else if(byPower){
            holding = true;
            left_hang.setPower(1);
            right_hang.setPower(1);
            left_hang.setTargetPosition(right_hang.getCurrentPosition());
            right_hang.setTargetPosition(right_hang.getCurrentPosition());
        }else if(movement > 4000){
            left_hang.setPower(1);
            right_hang.setPower(1);
            left_hang.setTargetPosition(6500);
            right_hang.setTargetPosition(6500);
        }else if(movement < 0){
            left_hang.setPower(1);
            right_hang.setPower(1);
            left_hang.setTargetPosition(0);
            right_hang.setTargetPosition(0);
        }else{
            left_hang.setPower(1);
            right_hang.setPower(1);
            left_hang.setTargetPosition((int)movement);
            right_hang.setTargetPosition((int)movement);
        }
    }

}