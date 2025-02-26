
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp(name= "TeleOpMain", group="Linear Opmode")
public class TeleOpMain extends LinearOpMode {

    // Declare OpMode members.
    private DcMotorEx.ZeroPowerBehavior brake = DcMotorEx.ZeroPowerBehavior.BRAKE;
    private DcMotorEx.ZeroPowerBehavior floatt = DcMotorEx.ZeroPowerBehavior.FLOAT;

    private DcMotorEx leftFront, leftRear, rightRear, rightFront; //wheels

    private DcMotorEx right_horizontal,left_horizontal; //slides

    private ServoImplEx rotate_floor, pinch_floor, flip_floor;

    ElapsedTime drawerTimer = new ElapsedTime();
    ElapsedTime servoTimer = new ElapsedTime();

    boolean motorState = true;

    int errorBound = 60;
    boolean holding = false;

    int h = 0;

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

        telemetry.update();

        rightFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightRear.setDirection(DcMotorEx.Direction.REVERSE);
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftRear.setDirection(DcMotorEx.Direction.REVERSE);

        right_horizontal.setDirection(DcMotorEx.Direction.FORWARD);
        left_horizontal.setDirection(DcMotorEx.Direction.FORWARD);

        right_horizontal.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left_horizontal.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        reset();

        servoTimer.reset();
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("right_horizontal: ", right_horizontal.getCurrentPosition());
                telemetry.addData("left_horizontal: ", left_horizontal.getCurrentPosition());
                telemetry.addData("mode: ", right_horizontal.getMode());
                telemetry.addData(" motor state: ", motorState);

                telemetry.update();

                if (gamepad2.triangle) {
                    move(740, false);
                    flip_floor.setPosition(0);
                    rotate_floor.setPosition(0.5);
                } else if (gamepad2.circle) {
                    move(350, false);
                } else if (gamepad2.cross) {
                    move(200, false);
                } else if(gamepad2.square){
                    flip_floor.setPosition(1);
                    drawerTimer.reset();
                    move(0, false);
                    if(drawersDone(right_horizontal, left_horizontal) && drawerTimer.seconds() > 2){
                        settle_slides();
                    }
                }
                else if(gamepad2.left_trigger > 0 || gamepad2.right_trigger > 0) {
                    move(gamepad2.left_trigger - gamepad2.right_trigger, true);
                }
                else if(gamepad2.dpad_up){
                    drawerTimer.reset();
                    move(0, false);
                    if(drawersDone(right_horizontal, left_horizontal) && drawerTimer.seconds() > 2){
                        settle_slides();
                    }
                }


//                else if(gamepad1.circle){
//                    move(right_horizontal.getCurrentPosition() + 100, false);
//                }

                //GAMEPAD1 CONTROLS
                //drivetrain, rotate_front, pinch_front,
                // all chamber controls - flipping should be macro with open / close

                //GAMEPAD1 CONTROLS
                //flip_front, horizontal slides, hang

                if(gamepad1.right_bumper){
                    pinch_floor.setPosition(1);
                }

                if(gamepad1.left_bumper){
                    pinch_floor.setPosition(0);
                }

                //drivetrain
                rightFront.setPower(((gamepad1.left_stick_y + gamepad1.left_stick_x)) + (gamepad1.right_stick_x));
                leftFront.setPower(((-gamepad1.left_stick_y + gamepad1.left_stick_x)) + ((gamepad1.right_stick_x)));
                rightRear.setPower(((gamepad1.left_stick_y + -gamepad1.left_stick_x)) + (gamepad1.right_stick_x));
                leftRear.setPower(((-gamepad1.left_stick_y + -gamepad1.left_stick_x)) + (gamepad1.right_stick_x));


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

    public void move(double movement, boolean byPower){
        holding = false;
        right_horizontal.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_horizontal.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(movement > 0 && byPower){
            setTargetPosition(4000, movement);
        }else if(movement < 0 && byPower){
            setTargetPosition(0, -movement);
        }else if(byPower){
            holding = true;
            setTargetPosition(right_horizontal.getCurrentPosition(), 0.5);
        }else if(movement > 4000){
            setTargetPosition(4000);
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
    public void setTargetPosition(int target){
        setPower(0.8);
        left_horizontal.setTargetPosition(target);
        right_horizontal.setTargetPosition(target);
    }

    public void setTargetPosition(int target, double power){
        setPower(power);
        left_horizontal.setTargetPosition(target);
        right_horizontal.setTargetPosition(target);
    }

}