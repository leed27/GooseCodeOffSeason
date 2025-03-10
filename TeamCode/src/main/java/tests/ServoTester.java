package tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name= "tests.ServoTester", group="Linear Opmode")
//@Disabled
public class ServoTester extends LinearOpMode {

    private ServoImplEx rotate_floor, pinch_floor, flip_floor, right_swing, left_swing, rotate_chamber, pinch_chamber;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        rotate_floor = hardwareMap.get(ServoImplEx.class, "rotate_floor");
        pinch_floor = hardwareMap.get(ServoImplEx.class, "pinch_floor");
        flip_floor = hardwareMap.get(ServoImplEx.class, "flip_floor");
        right_swing = hardwareMap.get(ServoImplEx.class, "right_swing");
        left_swing = hardwareMap.get(ServoImplEx.class, "left_swing");
        rotate_chamber = hardwareMap.get(ServoImplEx.class, "rotate_chamber");
        pinch_chamber = hardwareMap.get(ServoImplEx.class, "pinch_chamber");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("flip: ", flip_floor.getPosition());
                telemetry.addData("rotate: ", rotate_floor.getPosition());
                telemetry.addData("pinch: ", pinch_chamber.getPosition());
                telemetry.update();




                //FLIP
                if(gamepad1.triangle){
                    flip_floor.setPosition(1);
                }

                if(gamepad1.cross){
                    flip_floor.setPosition(0); //down position
                }

                if(gamepad1.square){
                    flip_floor.setPosition(0.5); // up pos
                }

                //ROTATE

                if(gamepad1.right_bumper){
                    rotate_floor.setPosition(0);
                }

                if(gamepad1.left_bumper){
                    rotate_floor.setPosition(1);
                }

                if(gamepad1.circle){
                    rotate_floor.setPosition(0.52); //start pos
                }

                //PINCH

                if(gamepad1.dpad_up){
                    pinch_floor.setPosition(1); //close
                }

                if(gamepad1.dpad_down){
                    pinch_floor.setPosition(0);
                }

                if(gamepad1.dpad_left){
                    pinch_floor.setPosition(0.5); //open
                }

                //GAMEPAD2 CONTROLS

                if(gamepad2.triangle){
                    rotate_chamber.setPosition(0);
                }

                if(gamepad2.cross){
                    rotate_chamber.setPosition(1);
                }

                if(gamepad2.circle){
                    rotate_chamber.setPosition(0.5);
                }

                if(gamepad2.dpad_up){
                    pinch_chamber.setPosition(0.95);
                }

                if(gamepad2.dpad_down){
                    pinch_chamber.setPosition(0);
                }

                if(gamepad2.dpad_left){
                    pinch_chamber.setPosition(0.5);
                }

                if(gamepad2.right_bumper){
                    left_swing.setPosition(0); // down
                    right_swing.setPosition(0);
                }

                if(gamepad2.left_bumper){
                    right_swing.setPosition(0.60);
                    left_swing.setPosition(0.60); // score
                }



            }

        }
    }
}