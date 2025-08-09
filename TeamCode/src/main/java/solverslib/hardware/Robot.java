package solverslib.hardware;

import static solverslib.hardware.Globals.*;

import solverslib.commandbase.Endgame;
import solverslib.commandbase.Intake;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.solversHardware.SolversAxonServo;
import com.seattlesolvers.solverslib.solversHardware.SolversMotorEx;

import java.util.List;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import solverslib.commandbase.Outtake;

public class Robot {

    public SolversMotorEx leftFront, leftRear, rightRear, rightFront; //drivetrain wheels

    public SolversMotorEx right_horizontal,left_horizontal; //horizontal slides

    public SolversMotorEx right_hang, left_hang;

    public ServoImplEx rotate_floor, pinch_floor, flip_floor, right_swing, left_swing, rotate_chamber, pinch_chamber;

    //try out may be cool
    public Motor.Encoder slidesEncoder;

    public Servo light1, light2;

    public Follower follower;
    public PoseUpdater poseUpdater;

    /// the next two are for optimizing loop times
    public List<LynxModule> allHubs;
    public LynxModule ControlHub;

    public Intake intake;
    public Outtake outtake;
    public Endgame endgame;

    private static Robot instance = new Robot();
    public boolean enabled;

    public static Robot getInstance() {
        if(instance == null){
            instance = new Robot();
        }
        instance.enabled = true;
        return instance;
    }

    /// run only after robot instance has been made
    public void init(HardwareMap hardwareMap) {
        rightFront = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "rightFront"), 0.01);
        leftFront = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "leftFront"), 0.01);
        rightRear = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "rightRear"), 0.01);
        leftRear = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "leftRear"), 0.01);

        right_horizontal = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "right_horizontal"), 0.01);
        left_horizontal = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "left_horizontal"), 0.01);

        rotate_floor = hardwareMap.get(ServoImplEx.class, "rotate_floor");
        pinch_floor = hardwareMap.get(ServoImplEx.class, "pinch_floor");
        flip_floor = hardwareMap.get(ServoImplEx.class, "flip_floor");

        right_swing = hardwareMap.get(ServoImplEx.class, "right_swing");
        left_swing = hardwareMap.get(ServoImplEx.class, "left_swing");

        rotate_chamber = hardwareMap.get(ServoImplEx.class, "rotate_chamber");
        pinch_chamber = hardwareMap.get(ServoImplEx.class, "pinch_chamber");

        right_hang = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "right_hang"));
        left_hang = new SolversMotorEx(hardwareMap.get(DcMotorEx.class, "left_hang"));

        light1 = hardwareMap.get(Servo.class, "light1");
        light2 = hardwareMap.get(Servo.class, "light2");

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

        slidesEncoder = new Motor(hardwareMap, "left_horizontal").encoder;

        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);

        poseUpdater = new PoseUpdater(hardwareMap);

        //for optimizing loop times
        // Bulk reading enabled!
        // AUTO mode will bulk read by default and will redo and clear cache once the exact same read is done again
        // MANUAL mode will bulk read once per loop but needs to be manually cleared
        // Also in opModes only clear ControlHub cache as it is a hardware write
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            if (hub.isParent() && LynxConstants.isEmbeddedSerialNumber(hub.getSerialNumber())) {
                ControlHub = hub;
            }

        }

        intake = new Intake();
        outtake = new Outtake();
        endgame = new Endgame();

        if(opModeType.equals(OpModeType.TELEOP)) {
            follower.startTeleopDrive();

            follower.setStartingPose(autoEndPose);
        } else{
            follower.setStartingPose(new Pose(0, 0, 0));
        }
    }

    /// RUN WHATEVER IS IN THE INIT METHODS IN THE SUBSYSTEMS!!
    public void initHasMovement() {
        intake.init();
    }
}
