package nextftc.subsystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;

public class Intake extends Subsystem {
    public static final Intake INSTANCE = new Intake();
    private Intake() { }

    public Servo claw, rotate, flip;

    public String clawName = "pinch_floor";
    public String rotateName = "pinch_floor";
    public String flipName = "flip_floor";

    public Command open() {
        return new ServoToPosition(claw, 0.45, this);
    }

    public Command close() {
        return new ServoToPosition(claw, 1, this);
    }



    @Override
    public void initialize() {
        claw = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, clawName);
        rotate = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, rotateName);
        flip = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, flipName);
    }
}
