import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

/**
 * This is an example auto that showcases movement and control of two servos autonomously.
 * It is a 0+4 (Specimen + Sample) bucket auto. It scores a neutral preload and then pickups 3 samples from the ground and scores them before parking.
 * There are examples of different ways to build paths.
 * A path progression method has been created and can advance based on time, position, or other factors.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 11/28/2024
 */

@Autonomous(name = "\u2757 ChamberPlswork \u2757", group = "Auton")
public class ChamberPlswork extends OpMode {

    public MotorMech2 slides;

    Servo light1, light2;

    private ServoImplEx rotate_floor, pinch_floor, flip_floor, right_swing, left_swing, rotate_chamber, pinch_chamber;


    private double cranePower = 1;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState, cycle_counter;

    /* Create and Define Poses + Paths
     * Poses are built with three constructors: x, y, and heading (in Radians).
     * Pedro uses 0 - 144 for x and y, with 0, 0 being on the bottom left.
     * (For Into the Deep, this would be Blue Observation Zone (0,0) to Red Observation Zone (144,144).)
     * Even though Pedro uses a different coordinate system than RR, you can convert any roadrunner pose by adding +72 both the x and y.
     * This visualizer is very easy to use to find and create paths/pathchains/poses: <https://pedro-path-generator.vercel.app/>
     * Lets assume our tests.robot is 18 by 18 inches
     * Lets assume the Robot is facing the human player and we want to score in the bucket */

    /** Start Pose of our tests.robot */
    private final Pose startPose = new Pose(9, 72, Math.toRadians(180));
    private final Pose scorePrePose = new Pose(40,72, Math.toRadians(180));

    private final Pose pushSplineControl1 = new Pose(20, 30);
    private final Pose pushSplineEnd = new Pose(30, 30, Math.toRadians(180));
    private final Pose returnFirst = new Pose(68,43);
    private final Pose strafeFirst = new Pose(56, 25);
    private final Pose pushFirst = new Pose(26, 26);
    private final Pose returnSecond = new Pose(58, 27);
    //    private final Pose returnSecond = new Pose(56, 25);
    private final Pose strafeSecond = new Pose(56, 15);
    private final Pose pushSecond =  new Pose(24, 15);
    private final Pose returnThird = new Pose(75, 17);
    private final Pose strafeThird = new Pose(70, 2);
    private final Pose pushThird =  new Pose(20, 8.5); // x = 17
    private final Pose grabSplineControl = new Pose(28, 32);

    private final Pose grabForwardPose = new Pose(9, 32, Math.toRadians(180));
    private final Pose grabForwardPose2 = new Pose(9, 32, Math.toRadians(180));
    private final Pose grabForwardPose3 = new Pose(9, 32, Math.toRadians(180));
    private final Pose grabForwardPose4 = new Pose(9, 32, Math.toRadians(180));
    private final Pose grabPose = new Pose(25, 32); //x = 30

    private final Pose scoreFirstPose = new Pose(40, 70, Math.toRadians(180));
    private final Pose scoreSecondPose = new Pose(40, 69, Math.toRadians(180));
    private final Pose safetyScore = new Pose(40, 69, Math.toRadians(180));
    private final Pose scoreThirdPose = new Pose(40, 68, Math.toRadians(180));
    private final Pose scoreFourthPose = new Pose(40, 67, Math.toRadians(180));
    private final Pose parkPose = new Pose(12, 10);


    /* These are our Paths and PathChains that we will define in buildPaths() */
    private Path scorePreload;
    private PathChain pushSpline, pushBlocks, grabSpline, scoreFirst, grabSecond, scoreSecond, grabThird, scoreThird, grabFourth, scoreFourth, parkGood;
    public void buildPaths() {
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePrePose)));
        scorePreload.setConstantHeadingInterpolation(Math.toRadians(180));

        pushSpline = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePrePose), new Point(pushSplineControl1), new Point(pushSplineEnd), new Point(returnFirst), new Point(strafeFirst)))
                .setConstantHeadingInterpolation(scorePrePose.getHeading())
                .build();
        pushBlocks  = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(strafeFirst), new Point(pushFirst)))
                .setPathEndTimeoutConstraint(100)
                .setPathEndTValueConstraint(0.95)
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .addPath(new BezierCurve(new Point(pushFirst), new Point(returnSecond), new Point(strafeSecond)))
                .setPathEndTimeoutConstraint(100)
                .setPathEndTValueConstraint(0.95)
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .addPath(new BezierLine(new Point(strafeSecond), new Point(pushSecond)))
                .setPathEndTimeoutConstraint(100)
                .setPathEndTValueConstraint(0.95)
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .addPath(new BezierCurve(new Point(pushSecond), new Point(returnThird), new Point(strafeThird), new Point(pushThird)))
                .setPathEndTimeoutConstraint(100)
                .setPathEndTValueConstraint(0.95)
                .setConstantHeadingInterpolation(Math.toRadians(180))
//                .addPath(new BezierLine(new Point(strafeThird), new Point(pushThird)))
//                .setPathEndTimeoutConstraint(100)
//                .setPathEndTValueConstraint(0.95)
//                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        grabSpline = follower.pathBuilder()
                //.addPath(new BezierCurve(new Point(pushThird), new Point(grabSplineControl), new Point(grabForwardPose)))
                .addPath(new BezierLine(new Point(pushThird), new Point(grabForwardPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        scoreFirst = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabForwardPose), new Point(scoreFirstPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        scoreSecond = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabForwardPose2), new Point(scoreSecondPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        scoreThird = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabForwardPose3), new Point(scoreThirdPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        scoreFourth = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabForwardPose4), new Point(scoreFourthPose)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        grabSecond = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scoreFirstPose), new Point(grabPose), new Point(grabForwardPose2)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                //.setZeroPowerAccelerationMultiplier(3.2)
                .build();
        grabThird = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scoreSecondPose), new Point(grabPose), new Point(grabForwardPose3)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                //.setZeroPowerAccelerationMultiplier(2)
                .build();
        grabFourth = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scoreThirdPose), new Point(grabPose), new Point(grabForwardPose4)))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                //.setZeroPowerAccelerationMultiplier(2)
                .build();
        parkGood = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabForwardPose), new Point(parkPose)))
                .build();

    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                right_swing.setPosition(0.52);
                left_swing.setPosition(0.52);
                if (pathTimer.getElapsedTimeSeconds() > 0.25) {
                    rotate_chamber.setPosition(0.8);
                }
                if(pathTimer.getElapsedTimeSeconds() > 0.4) {
                    follower.followPath(scorePreload, true);
                    setPathState(1);
                }
                break;
            case 1:
                if(!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.2){
                    right_swing.setPosition(0.65);
                    left_swing.setPosition(0.65);

                    if (pathTimer.getElapsedTimeSeconds() > 1.6){
                        pinch_chamber.setPosition(0.5);

                        right_swing.setPosition(0.07);
                        left_swing.setPosition(0.07);
                    }

                    if(pathTimer.getElapsedTimeSeconds() > 1.8){
                        rotate_chamber.setPosition(0);
                        setPathState(2);
                    }
                }
                break;
            case 2:
                if(!follower.isBusy()){
                    follower.followPath(pushSpline, false);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()){
                    follower.followPath(pushBlocks, false);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy()){
                    follower.followPath(grabSpline, true);
                    setPathState(5);
                }
                break;
            case 5:
                //grabs specimen off the wall
                if(follower.getPose().getX() < 11 && cycle_counter != 0){
                    pinch_chamber.setPosition(0.95);

                    if (pathTimer.getElapsedTimeSeconds() > 2) {
                        right_swing.setPosition(0.52); //prep score
                        left_swing.setPosition(0.52);
                    }

                    if(pathTimer.getElapsedTimeSeconds() > 2.1){
                        rotate_chamber.setPosition(0.8);
                        cycle_counter++;
                        setPathState(6);
                    }
                }
                break;
            case 6:
                //moves to chamber to score
                    if(cycle_counter == 1){
                        follower.followPath(scoreFirst, true);
                        telemetry.update();
                        setPathState(7);
                    }
                    else if(cycle_counter == 2){
                        follower.followPath(scoreSecond, true);
                        setPathState(7);
                    }
                    else if(cycle_counter == 3){
                        follower.followPath(scoreThird, true);
                        setPathState(7);
                    }
                    else if(cycle_counter == 4){
                        follower.followPath(scoreFourth, true);
                        setPathState(7);
                    }
                break;
            case 7: //Scores specimen
                if(!follower.isBusy()){
                        right_swing.setPosition(0.65);
                        left_swing.setPosition(0.65);
                        if(pathTimer.getElapsedTimeSeconds() > 2.25){
                            setPathState(8);
                        }
                }
                break;
            case 8:
                 pinch_chamber.setPosition(0.5);

                 right_swing.setPosition(0.07);
                 left_swing.setPosition(0.07);

                setPathState(23);

            case 23:
                rotate_chamber.setPosition(0);

                if(pathTimer.getElapsedTimeSeconds() > 3){
                    setPathState(9);
                }
            case 9:
                    if(cycle_counter == 1){
                        follower.followPath(grabSecond, true);
                        setPathState(5);
                    }
                    else if(cycle_counter == 2){
                        follower.followPath(grabThird, true);
                        setPathState(5);
                    }
                    else if(cycle_counter == 3){
                        follower.followPath(grabFourth, true);
                        setPathState(5);
                    }
                    else if(cycle_counter == 4){
                        follower.followPath(parkGood, true);
                        setPathState(10);
                    }
                break;
            case 10:
                if(!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 2){
                    setPathState(-1);
                }
                break;
        }
    }

    /** These change the states of the paths and actions
     * It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {
        light1.setPosition(0);
        light2.setPosition(0);

        follower.update();
        autonomousPathUpdate();
        telemetry.addData("Path State", pathState);
        telemetry.addData("Position", follower.getPose().toString());
        telemetry.addData("cycle_counter", cycle_counter);
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

        cycle_counter = 0;

        slides = new MotorMech2(hardwareMap, cranePower, false);

        rotate_floor = hardwareMap.get(ServoImplEx.class, "rotate_floor");
        pinch_floor = hardwareMap.get(ServoImplEx.class, "pinch_floor");
        flip_floor = hardwareMap.get(ServoImplEx.class, "flip_floor");

        right_swing = hardwareMap.get(ServoImplEx.class, "right_swing");
        left_swing = hardwareMap.get(ServoImplEx.class, "left_swing");

        rotate_chamber = hardwareMap.get(ServoImplEx.class, "rotate_chamber");
        pinch_chamber = hardwareMap.get(ServoImplEx.class, "pinch_chamber");

        light1 = hardwareMap.get(Servo.class, "light1");
        light2 = hardwareMap.get(Servo.class, "light2");

        //rotate_floor.setPosition(0.1);
        flip_floor.setPosition(0.5);
        rotate_chamber.setPosition(0);
        right_swing.setPosition(0.18);
        left_swing.setPosition(0.18);
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {
        if(gamepad2.circle){
            pinch_chamber.setPosition(0.95);
        }

        //light1.setPosition(0.722);
        //light2.setPosition(0.722);
    }

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
    }
}