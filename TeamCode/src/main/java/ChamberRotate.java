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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "ChamberRotate", group = "Pedro")
public class ChamberRotate extends OpMode {

    private Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer;

    private double cranePower = 1;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState, cycle_counter;

    /** This is our subsystem.
     * We call its methods to manipulate the stuff that it has within the subsystem. */
    public MotorMech2 slides;

    private ServoImplEx rotate_floor, pinch_floor, flip_floor, right_swing, left_swing, rotate_chamber, pinch_chamber;

    private final Pose startPose = new Pose(9, 72, Math.toRadians(180));
    private final Pose score1Pose = new Pose(38, 71, Math.toRadians(180));
    private final Pose score2Pose = new Pose(38, 69, Math.toRadians(180));
    private final Pose score3Pose = new Pose(38, 68, Math.toRadians(180));
    private final Pose score4Pose = new Pose(38, 67, Math.toRadians(180));
    private final Pose score5Pose = new Pose(38, 73, Math.toRadians(0));



    /** Grabbing the specimen from the observation zone */
    private final Pose grabBackPose = new Pose(20, 32, Math.toRadians(0));
    private final Pose grabPose = new Pose(10.5, 32, Math.toRadians(0));


    /** Poses for pushing the samples */
    private final Pose pushPose1 = new Pose(20, 65, Math.toRadians(-45));
    private final Pose pushForwardPose1 = new Pose(21, 65, Math.toRadians(-135));
    private final Pose pushPose2 = new Pose(25, 55, Math.toRadians(-45));
    private final Pose pushForwardPose2 = new Pose(48, 55, Math.toRadians(-95));
    private final Pose pushPose3 = new Pose(25, 45, Math.toRadians(-45));
    private final Pose pushForwardPose3 = new Pose(30, 30, Math.toRadians(-135));
    private final Pose moveBackPose = new Pose(20, 20, Math.toRadians(0));

    /** Pose for maneuvering around the submersible */
    private final Pose maneuverPose = new Pose(58, 36.5, Math.toRadians(0));
    /** Maneuver Control Pose for our tests.robot, this is used to manipulate the bezier curve that we will create for the maneuver.
     * The Robot will not go to this pose, it is used a control point for our bezier curve. */
    private final Pose maneuverControlPose = new Pose(13, 25, Math.toRadians(0));


    private final Pose parkPose = new Pose(12, 30, Math.toRadians(0));

    /* These are our Paths and PathChains that we will define in buildPaths() */
    private Path scorePreload, maneuver, park;
    private PathChain moveFirstBlock, moveFirstBlockNet, moveSecondBlock, moveSecondBlockNet, moveThirdBlock, moveThirdBlockNet, grabSpecimen1, grabSpecimen2, grabSpecimen3, grabSpecimen4, scoreSpecimen1, scoreSpecimen2, scoreSpecimen3, scoreSpecimen4;

    /** Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts. **/
    public void buildPaths() {

        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(score1Pose)));
        //scorePreload.setZeroPowerAccelerationMultiplier(3);
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), score1Pose.getHeading());


        /* This is our moveBlocks PathChain. We are using multiple paths with a BezierLine, which is a straight line. */
        moveFirstBlock = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score1Pose), new Point(pushPose1)))
                .setLinearHeadingInterpolation(score1Pose.getHeading(), pushPose1.getHeading(), 0.5)
                .build();

        moveFirstBlockNet = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pushPose1), new Point(pushForwardPose1)))
                .setLinearHeadingInterpolation(pushPose1.getHeading(), pushForwardPose1.getHeading(), 0.5)
                .build();

        moveSecondBlock = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pushForwardPose1), new Point(pushPose2)))
                .setLinearHeadingInterpolation(pushForwardPose1.getHeading(), pushPose2.getHeading(), 0.5)
                .build();

        moveSecondBlockNet = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pushPose2), new Point(pushForwardPose2)))
                .setLinearHeadingInterpolation(pushPose2.getHeading(), pushForwardPose2.getHeading(), 0.5)
                .build();

        moveThirdBlock = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pushForwardPose2), new Point(pushPose3)))
                .setPathEndTimeoutConstraint(200)
                .setLinearHeadingInterpolation(pushForwardPose2.getHeading(), pushPose3.getHeading(), 0.5)
                .build();

        moveThirdBlockNet = follower.pathBuilder()
                .addPath(new BezierLine(new Point(pushPose3), new Point(pushForwardPose3)))
                .setLinearHeadingInterpolation(pushPose3.getHeading(), pushForwardPose3.getHeading(), 0.5)
                .build();

        /* This is our grabSpecimen1 PathChain. We are using a single path with a BezierLine, which is a straight line. */
        grabSpecimen1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score1Pose), new Point(grabBackPose)))
                .setLinearHeadingInterpolation(score1Pose.getHeading(), grabPose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .addPath(new BezierLine(new Point(grabBackPose), new Point(grabPose)))
                .setLinearHeadingInterpolation(grabBackPose.getHeading(), grabPose.getHeading())
                .build();

        scoreSpecimen1 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabPose), new Point(score2Pose)))
                .setLinearHeadingInterpolation(grabPose.getHeading(), score2Pose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .build();

        grabSpecimen2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score2Pose), new Point(grabBackPose)))
                .setLinearHeadingInterpolation(score2Pose.getHeading(), grabPose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .addPath(new BezierLine(new Point(grabBackPose), new Point(grabPose)))
                .setLinearHeadingInterpolation(grabBackPose.getHeading(), grabPose.getHeading())
                .build();

        scoreSpecimen2 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabPose), new Point(score3Pose)))
                .setLinearHeadingInterpolation(grabPose.getHeading(), score3Pose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .build();

        grabSpecimen3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score3Pose), new Point(grabBackPose)))
                .setLinearHeadingInterpolation(score3Pose.getHeading(), grabPose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .addPath(new BezierLine(new Point(grabBackPose), new Point(grabPose)))
                .setLinearHeadingInterpolation(grabBackPose.getHeading(), grabPose.getHeading())
                .build();

        scoreSpecimen3 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabPose), new Point(score4Pose)))
                .setLinearHeadingInterpolation(grabPose.getHeading(), score4Pose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .build();

        grabSpecimen4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(score4Pose), new Point(grabBackPose)))
                .setLinearHeadingInterpolation(score4Pose.getHeading(), grabPose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .addPath(new BezierLine(new Point(grabBackPose), new Point(grabPose)))
                .setLinearHeadingInterpolation(grabBackPose.getHeading(), grabPose.getHeading())
                .build();
        scoreSpecimen4 = follower.pathBuilder()
                .addPath(new BezierLine(new Point(grabPose), new Point(score5Pose)))
                .setLinearHeadingInterpolation(grabPose.getHeading(), score5Pose.getHeading(), 0.7)
                .setPathEndTimeoutConstraint(400)
                .setPathEndHeadingConstraint(.001)
                .build();

        park = new Path(new BezierLine(new Point(score2Pose), new Point(parkPose)));
        park.setTangentHeadingInterpolation();
        //TODO: change back if necessary
    }

    /** This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on. */
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
                    right_swing.setPosition(0.70);
                    left_swing.setPosition(0.70);

                    if (pathTimer.getElapsedTimeSeconds() > 1.7 && right_swing.getPosition() == 0.7) {
                        pinch_chamber.setPosition(0.5);

                        right_swing.setPosition(0.07);
                        left_swing.setPosition(0.07);
                    }
                    if (pathTimer.getElapsedTimeSeconds() > 1.9) {
                        follower.followPath(moveFirstBlock, true);
                        setPathState(2);
                    }


                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    rotate_chamber.setPosition(0);
                    slides.setTargetPosition(700);
                    flip_floor.setPosition(0.1);
                    if(pathTimer.getElapsedTimeSeconds() > 3){
                        pinch_floor.setPosition(1); //close

                    }
                    if(pathTimer.getElapsedTimeSeconds() > 3.2) {
                        follower.followPath(moveFirstBlockNet, true);
                        //setPathState(3);
                    }
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    pinch_floor.setPosition(0.4); //open
                    follower.followPath(moveSecondBlock, true);
                    setPathState(4);

                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 1.7) {
                        follower.followPath(moveSecondBlockNet, true);
                        setPathState(5);
                    }
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(moveThirdBlock, true);
                    setPathState(6);

                }
                break;
            case 6:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 1.9) {
                        follower.followPath(moveThirdBlockNet, true);
                        setPathState(7);
                    }
                }
                break;
            case 7:
                if(!follower.isBusy()) {
                    follower.followPath(grabSpecimen1, true);
                    setPathState(8);
                }
                break;
            case 8:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 2.2){
                        follower.followPath(scoreSpecimen1, true);
                        setPathState(9);
                    }
                }
                break;
            case 9:
                if(!follower.isBusy()) {
                    /* Grab Sample */
                    if (pathTimer.getElapsedTimeSeconds() > 4) {
                        follower.followPath(grabSpecimen2, true);
                        setPathState(10);
                    }

                }
                break;
            case 10:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 2.2){
                    }
                    if(pathTimer.getElapsedTimeSeconds() > 2.4){
                        follower.followPath(scoreSpecimen2, true);
                        setPathState(11);
                    }
                }
                break;
            case 11:
                if(!follower.isBusy()) {
                    /* Grab Sample */
                    follower.followPath(grabSpecimen3, true);
                    setPathState(12);
                }
                break;
            case 12:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 2.2){
                    }
                    if(pathTimer.getElapsedTimeSeconds() > 2.4){
                        follower.followPath(scoreSpecimen3, true);
                        setPathState(13);
                    }
                }
                break;
            case 13:
                if(!follower.isBusy()) {
                    /* Grab Sample */
                    if (pathTimer.getElapsedTimeSeconds() > 3) {
                    }
                    if (pathTimer.getElapsedTimeSeconds() > 4) {
                        follower.followPath(grabSpecimen4, true);
                        setPathState(14);
                    }
                }
                break;
            case 14:
                if(!follower.isBusy()) {
                    if(pathTimer.getElapsedTimeSeconds() > 2.2){
                    }
                    if(pathTimer.getElapsedTimeSeconds() > 2.4){
                        follower.followPath(scoreSpecimen4, true);
                        setPathState(15);
                    }
                }
                break;
            case 16:
                /* This case checks the tests.robot's position and will wait until the tests.robot position is close (1 inch away) from the scorePose's position */
                if(!follower.isBusy()) {
                    /* Grab Sample */
                    if (pathTimer.getElapsedTimeSeconds() > 4) {
                        /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */
                        follower.followPath(park, true);
                        setPathState(-1);
                    }



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

        rotate_floor.setPosition(0.1);
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