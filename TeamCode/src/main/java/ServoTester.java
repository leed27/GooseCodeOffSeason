import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name= "ServoTester", group="Linear Opmode")
//@Disabled
public class ServoTester extends LinearOpMode {


    private ServoImplEx backR, backL, extendR, extendL;


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        backR = hardwareMap.get(ServoImplEx.class, "backR");
        backL = hardwareMap.get(ServoImplEx.class, "backL");
        backR = hardwareMap.get(ServoImplEx.class, "backR");
        backL = hardwareMap.get(ServoImplEx.class, "backL");
        extendR = hardwareMap.get(ServoImplEx.class, "extendR");
        extendL = hardwareMap.get(ServoImplEx.class, "extendL");

        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetry.addData("Status", "Running");
                telemetry.addData("right: ", extendR.getPosition());
                telemetry.addData("left: ", extendL.getPosition());
                telemetry.update();

                if(gamepad1.dpad_up){
                    extendL.setPosition(1);
                }

                if(gamepad1.dpad_down){
                    extendL.setPosition(0); // out position
                }

                if(gamepad1.triangle){
                    extendR.setPosition(1); // out position
                }

                if(gamepad1.cross){
                    extendR.setPosition(0);
                }

                if(gamepad1.right_bumper){
                    extendR.setPosition(0);
                    extendL.setPosition(1);

                }

                if(gamepad1.left_bumper){
                    extendR.setPosition(0.05);
                    extendL.setPosition(0.95);
                }
//

//                //BACK CLAWS
//                if(gamepad1.right_bumper){
//                    backR.setPosition(1);
//                    backL.setPosition(0);
//
//                }
//
//                if(gamepad1.left_bumper){
//                    backR.setPosition(0.75);
//                    backL.setPosition(0.30);
//                }


//                FRONT CLAWS
//                if(gamepad1.right_bumper){
//                    frontR.setPosition(1);
//                    frontL.setPosition(0);
//
//                }
//
//                if(gamepad1.left_bumper){
//                    frontR.setPosition(0.5);
//                    frontL.setPosition(0.35);
//                }


            }

        }
    }
}