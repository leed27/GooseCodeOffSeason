package solverslib.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.localization.Pose;

@Config
public class Globals {
    public enum OpModeType {
        AUTO,
        TELEOP
    }

    public static OpModeType opModeType;

    public static Pose autoEndPose = new Pose(0, 0, Math.toRadians(0));

    public static double SLIDES_IN = 0;
    public static double SLIDES_SHORT = 250;
    public static double SLIDES_MEDIUM = 450;
    public static double SLIDES_LONG = 550;

    //servo positions
    public static double INTAKE_OPEN = 0.45;
    public static double INTAKE_CLOSE = 1;
    public static double INTAKE_DOWN = 0.5;
    public static double INTAKE_UP = 0.1;
    //90 degree rotation
    public static double INTAKE_ROTATED = 0;
    public static double INTAKE_NOTROTATED = 0.5;
    public static double OUTTAKE_OPEN = 0.5;
    public static double OUTTAKE_CLOSE = 0.95;
    public static double OUTTAKE_ARM_DOWN = 0.07;
    public static double OUTTAKE_ARM_HOVER = 0.52;
    public static double OUTTAKE_ARM_SCORE = 0.76;
    public static double OUTTAKE_ROTATED = 0.8;
    public static double OUTTAKE_NOTROTATED = 0;
}
