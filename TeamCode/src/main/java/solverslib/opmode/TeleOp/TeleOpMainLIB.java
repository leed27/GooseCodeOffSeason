package solverslib.opmode.TeleOp;

import static solverslib.hardware.Globals.*;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ConditionalCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import solverslib.commandbase.commands.GrabSpecimen;
import solverslib.commandbase.commands.ScoreSpecimen;
import solverslib.hardware.Robot;

@TeleOp(name = "TeleOp")
public class TeleOpMainLIB extends CommandOpMode {
    public GamepadEx driver;

    public ElapsedTime gameTimer;

    private final Robot robot = Robot.getInstance();

    private boolean endgame = false;

    @Override
    public void initialize(){
        opModeType = OpModeType.TELEOP;


        // DO NOT REMOVE! Resetting FTCLib Command Sechduler
        super.reset();

        robot.init(hardwareMap);

        register(robot.intake);

        driver = new GamepadEx(gamepad1);

        /// IF THERE NEEDS TO BE MOVEMENT DURING INIT STAGE, UNCOMMENT
        //robot.initHasMovement();

        /// ALL CONTROLS

        driver.getGamepadButton(GamepadKeys.Button.TRIANGLE).whenPressed(
                new ConditionalCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> robot.intake.setSlideTarget(SLIDES_LONG)),
                                new WaitCommand(1000),
                                new InstantCommand(() -> robot.intake.clawDown(true))
                        ),
                        //only runs the second section if the robot is in endgame
                        new InstantCommand(() -> robot.endgame.setSlideTarget(6800)),
                        (() -> !endgame)
                )

        );

        driver.getGamepadButton(GamepadKeys.Button.CIRCLE).whenPressed(
                new ConditionalCommand(
                    new SequentialCommandGroup(
                            new InstantCommand(() -> robot.intake.setSlideTarget(SLIDES_MEDIUM)),
                            new WaitCommand(1000),
                            new InstantCommand(() -> robot.intake.clawDown(true))
                    ),
                    //only runs the second section if the robot is in endgame
                    new InstantCommand(() -> robot.endgame.setSlideTarget(850)),
                    (() -> !endgame)
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.CROSS).whenPressed(
                new ConditionalCommand(
                    new SequentialCommandGroup(
                            new InstantCommand(() -> robot.intake.setSlideTarget(SLIDES_SHORT)),
                            new WaitCommand(1000),
                            new InstantCommand(() -> robot.intake.clawDown(true))
                    ),
                        //only runs the second section if the robot is in endgame
                        new InstantCommand(() -> robot.endgame.setSlideTarget(8900)),
                        (() -> !endgame)
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.SQUARE).whenPressed(
                new SequentialCommandGroup(
                        new InstantCommand(() -> robot.intake.rotateClaw(INTAKE_NOTROTATED)),
                        new WaitCommand(500),
                        new InstantCommand(() -> robot.intake.clawDown(false)),
                        new WaitCommand(500),
                        new InstantCommand(() -> robot.intake.setSlideTarget(SLIDES_IN))

                )
        );

        driver.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(
                new InstantCommand(() -> robot.intake.rotateClaw(INTAKE_ROTATED))
        );

        driver.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(
                new InstantCommand(() -> robot.intake.rotateClaw(INTAKE_NOTROTATED))
        );


        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(
                new ConditionalCommand(
                    new InstantCommand(() -> robot.intake.rotateClaw(INTAKE_OPEN)),
                    new InstantCommand(() -> robot.intake.setSlideTarget(SLIDES_IN)),
                    (() -> !endgame)
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(
                new ConditionalCommand(
                    new InstantCommand(() -> robot.intake.rotateClaw(INTAKE_CLOSE)),
                    new InstantCommand(() -> robot.endgame.balance()),
                    (() -> !endgame)
                )
        );


        /// the cleanest way to format for now, apparently new release in sept that makes this neater
        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.5).whenActive(
                new GrabSpecimen(robot)
        );

        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.5).whenActive(
                new ScoreSpecimen(robot)
        );


        driver.getGamepadButton(GamepadKeys.Button.TOUCHPAD).whenPressed(
                new ConditionalCommand(
                        //if the condition (!endgame) is true then run this:
                        new SequentialCommandGroup(
                                new InstantCommand(() -> robot.flip_floor.setPosition(0)),
                                new InstantCommand(() -> robot.right_swing.setPosition(0.7)),
                                new InstantCommand(() -> endgame = true)
                        ),
                        //else run this:
                        new InstantCommand(() -> endgame = false),
                        (() -> !endgame)
                )
        );


        super.run();
    }

    @Override
    public void run() {
        // Keep all the has movement init for until when TeleOp starts
        // This is like the init but when the program is actually started
        if (gameTimer == null) {
            robot.initHasMovement();

            gameTimer = new ElapsedTime();
        }

        // DO NOT REMOVE! Runs FTCLib Command Scheudler
        super.run();

        telemetry.addData("Status", "Running");
        telemetry.addData("right_horizontal: ", robot.right_horizontal.getPosition());
        telemetry.addData("left_horizontal: ", robot.left_horizontal.getPosition());
        telemetry.addData("right_hang: ", robot.right_hang.getPosition());
        telemetry.addData("left_hang: ", robot.left_hang.getPosition());

        telemetry.update();

        // DO NOT REMOVE! Removing this will return stale data since bulk caching is on Manual mode
        // Also only clearing the control hub to decrease loop times
        // This means if we start reading both hubs (which we aren't) we need to clear both
        //robot.ControlHub.clearBulkCache();
        for(LynxModule hub : robot.allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void end() {
        autoEndPose = robot.follower.getPose();
    }
}
