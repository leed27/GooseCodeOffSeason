
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

    private DcMotorEx leftFront, leftBack, rightBack, rightFront; //wheels

    private DcMotorEx right, left, hang; //slides

    private ServoImplEx backR, backL, frontR, frontL, extendR, extendL;

    private int errorBound = 60;
    int height;
    boolean holding = false;
    //you can delete these two if you want, they're used for craig's button thingy.
    boolean clawState = false;
    boolean motorState = true;
    boolean pressed = false;
    int notPressed = 0;

    public enum state {
        PRESET,
        CUSTOM
    };

    state drawerState = state.PRESET;

    ElapsedTime drawerTimer = new ElapsedTime();
    ElapsedTime servoTimer = new ElapsedTime();

    ElapsedTime slideTimer = new ElapsedTime();

    int h = 0;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        drawerTimer.reset();

        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        right = hardwareMap.get(DcMotorEx.class, "right");
        left = hardwareMap.get(DcMotorEx.class, "left");
        hang = hardwareMap.get(DcMotorEx.class, "hang");
        backR = hardwareMap.get(ServoImplEx.class, "backR");
        backL = hardwareMap.get(ServoImplEx.class, "backL");
        frontR = hardwareMap.get(ServoImplEx.class, "frontR");
        frontL = hardwareMap.get(ServoImplEx.class, "frontL");
        extendL = hardwareMap.get(ServoImplEx.class, "extendR");
        extendR = hardwareMap.get(ServoImplEx.class, "extendL");

        telemetry.update();

        rightFront.setDirection(DcMotorEx.Direction.REVERSE);
        rightBack.setDirection(DcMotorEx.Direction.REVERSE);
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftBack.setDirection(DcMotorEx.Direction.REVERSE);

        right.setDirection(DcMotorEx.Direction.REVERSE);
        left.setDirection(DcMotorEx.Direction.FORWARD);

        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        reset();

        right.setCurrentAlert(1, CurrentUnit.AMPS);

        hang.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hang.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hang.setTargetPosition(0);
        //hang.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hang.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hang.setDirection(DcMotorEx.Direction.REVERSE);

        servoTimer.reset();
        telemetry.update();
        drawerState = state.PRESET;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("right: ", right.getCurrentPosition());
                telemetry.addData("left: ", left.getCurrentPosition());
                telemetry.addData("hang: ", hang.getCurrentPosition());
                telemetry.addData("mode: ", right.getMode());
                telemetry.addData(" motor state: ", motorState);

                telemetry.update();

                if (gamepad2.triangle) {
                    move(4000, false);
                    extendR.setPosition(0.965);
                    extendL.setPosition(0.035);
                } else if (gamepad2.circle) {
                    move(1825, false);
                } else if (gamepad2.cross) {
                    move(1500, false);
                } else if(gamepad2.square){
                    drawerTimer.reset();
                    move(0, false);
                    if(drawersDone(right, left) && drawerTimer.seconds() > 2){
                        settle_slides();
                    }
                }
                else if(gamepad2.right_bumper){
                    move(600, false);

                    backR.setPosition(1);
                    backL.setPosition(0);
                }
                else if (gamepad1.cross){
                    move(600, false);
                }
                else if(gamepad1.circle){
                    move(right.getCurrentPosition() + 100, false);
                }
                else if(gamepad1.dpad_up){
                    //move(2800, false);
                    movevertically(hang, 4700, 1);
                    h = 4700;

                }
                else if(gamepad1.dpad_down){
                    //move(2230, false);
                    movevertically(hang, 3175, 1);
                    h = 3175;
                }
                else if(gamepad1.dpad_left){
                    movevertically(hang, -h, 1);
                    h = 0;
                }
                else if(gamepad2.left_trigger > 0 || gamepad2.right_trigger > 0) {
                    move(gamepad2.left_trigger - gamepad2.right_trigger, true);
                }
                else if(!holding){
                    move(0, true);
                }
                else if(gamepad2.dpad_up){
                    drawerTimer.reset();
                    move(0, false);
                    if(drawersDone(right, left) && drawerTimer.seconds() > 2){
                        settle_slides();
                    }

                    extendR.setPosition(0.950);
                    extendL.setPosition(0.050);
                }
                else if(gamepad2.dpad_right){
                    extendR.setPosition(0.965);
                    extendL.setPosition(0.035);
                }
                else if(gamepad2.dpad_down){
                    move(300, false);

                    if(right.getCurrentPosition() > 270){
                        extendR.setPosition(1);
                        extendL.setPosition(0);
                    }
                }
                else if  (gamepad1.left_trigger > 0){
                    move(1500, false);

                    if(right.getCurrentPosition() < 1600){
                        backR.setPosition(1);
                        backL.setPosition(0);
                    }
                }

                //GAMEPAD1 CONTROLS

                //drivetrain
                rightFront.setPower(((gamepad1.left_stick_y + gamepad1.left_stick_x)) + (gamepad1.right_stick_x));
                leftFront.setPower(((-gamepad1.left_stick_y + gamepad1.left_stick_x)) + ((gamepad1.right_stick_x)));
                rightBack.setPower(((gamepad1.left_stick_y + -gamepad1.left_stick_x)) + (gamepad1.right_stick_x));
                leftBack.setPower(((-gamepad1.left_stick_y + -gamepad1.left_stick_x)) + (gamepad1.right_stick_x));

                //servos

                if(gamepad1.left_bumper){
                    frontR.setPosition(1);
                    frontL.setPosition(0);
                    clawState = false;
                }

                if(gamepad1.right_bumper){
                    if(clawState){
                        frontR.setPosition(0.80);
                        frontL.setPosition(0.25);

                    }else{
                        frontR.setPosition(0.60);
                        frontL.setPosition(0.45);
                    }
                    if(notPressed>20){
                        clawState = !clawState;
                    }
                    notPressed = 0;

                }else{
                    if(notPressed > 39){
                        notPressed = 40;
                    }else{
                        notPressed +=1;
                    }


                    if(gamepad1.touchpad){
                        if(motorState){
                            reset();
                            right.setMotorDisable();
                            left.setMotorDisable();

                        }else{
                            right.setMotorEnable();
                            left.setMotorEnable();
                        }
                        if(notPressed>20){
                            motorState = !motorState;
                        }
                        notPressed = 0;

                    }else {
                        if (notPressed > 39) {
                            notPressed = 40;
                        } else {
                            notPressed += 1;
                        }
                    }

                    if(gamepad2.touchpad){
                        if(motorState){
                            right.setMotorDisable();
                            left.setMotorDisable();

                        }else{
                            right.setMotorEnable();
                            left.setMotorEnable();
                        }
                        if(notPressed>20){
                            motorState = !motorState;
                        }
                        notPressed = 0;

                    }else {
                        if (notPressed > 39) {
                            notPressed = 40;
                        } else {
                            notPressed += 1;
                        }
                    }

                }
                telemetry.addData("notpressed", notPressed);


                if(gamepad1.right_trigger > 0){
                    backR.setPosition(0.73); //75
                    backL.setPosition(0.32); //30
                }

            }

        }
    }

    public void reset(){
        right.setPower(0);
        left.setPower(0);

        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        right.setTargetPosition(0);
        left.setTargetPosition(0);

        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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
        if(left.getCurrentPosition() < 25 && left.getCurrentAlert(CurrentUnit.AMPS) > 0.5 && left.getTargetPosition() == 0){
            left.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            left.setTargetPosition(0);
            left.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            left.setPower(0);

        }
        if(right.getCurrentPosition() < 25 && right.getCurrentAlert(CurrentUnit.AMPS) > 0.5 && left.getTargetPosition() == 0){
            right.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            left.setTargetPosition(0);
            right.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            right.setPower(0);
        }
    }

    public void move(double movement, boolean byPower){
        holding = false;
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(movement > 0 && byPower){
            setTargetPosition(4000, movement);
        }else if(movement < 0 && byPower){
            setTargetPosition(0, -movement);
        }else if(byPower){
            holding = true;
            setTargetPosition(right.getCurrentPosition(), 0.5);
        }else if(movement > 4000){
            setTargetPosition(4000);
        }else if(movement < 0){
            setTargetPosition(0);
        }else{
            setTargetPosition((int)movement);
        }
    }
    public void setPower(double power){
        left.setPower(power);
        right.setPower(power);
    }
    public void setTargetPosition(int target){
        setPower(0.8);
        left.setTargetPosition(target);
        right.setTargetPosition(target);
    }

    public void setTargetPosition(int target, double power){
        setPower(power);
        left.setTargetPosition(target);
        right.setTargetPosition(target);
    }

}