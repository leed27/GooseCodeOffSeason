import com.pedropathing.follower.FollowerConstants;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;


public class robot {
    private DcMotorEx leftFront, leftRear, rightFront, rightRear;
    private DcMotorEx right_horizonal, left_horizontal;
    private ServoImplEx rotate_chamber, pinch_chamber, rotate_floor, pinch_floor, flip_floor;
}