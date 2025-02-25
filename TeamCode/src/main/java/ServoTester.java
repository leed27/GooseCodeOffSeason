import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name= "ServoTester", group="Linear Opmode")
//@Disabled
public class ServoTester extends LinearOpMode {

    private ServoImplEx rotate_floor, pinch_floor, flip_floor, extendL;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        rotate_floor = hardwareMap.get(ServoImplEx.class, "rotate_floor");
        pinch_floor = hardwareMap.get(ServoImplEx.class, "pinch_floor");
        flip_floor = hardwareMap.get(ServoImplEx.class, "flip_floor");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("flip: ", flip_floor.getPosition());
                telemetry.addData("rotate: ", rotate_floor.getPosition());
                telemetry.addData("pinch: ", pinch_floor.getPosition());
                telemetry.update();


                //FLIP
                if(gamepad1.triangle){
                    flip_floor.setPosition(1);
                }

                if(gamepad1.cross){
                    flip_floor.setPosition(0);
                }

                if(gamepad1.square){
                    flip_floor.setPosition(0.5);
                }

                //ROTATE

                if(gamepad1.right_bumper){
                    rotate_floor.setPosition(0);
                }

                if(gamepad1.left_bumper){
                    rotate_floor.setPosition(1);
                }

                //PINCH

                if(gamepad1.dpad_up){
                    pinch_floor.setPosition(1);
                }

                if(gamepad1.dpad_down){
                    pinch_floor.setPosition(0);
                }

                if(gamepad1.dpad_left){
                    pinch_floor.setPosition(0.5);
                }


            }

        }
    }
}