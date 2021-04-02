package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Class for drive functionality. Called in other classes.
 */

public class Intake {
    Hardware robot;
    Telemetry telemetry;

    double speed = 1.0;
    boolean slowWasPressed = false;
    boolean isSlow = false;

    public Intake(Hardware hardware, Telemetry telemetryInstance) {
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void intake(boolean in, boolean out, boolean slowMode) {

        if (slowMode) {
            if (!slowWasPressed) {
                if (isSlow) {
                    speedUp();
                } else {
                    slowDown();
                }
            }
            slowWasPressed = true;
        } else {
            slowWasPressed = false;
        }

        if (in) {
            takeIn();
        } else if (out){
            takeOut();
        } else {
            fullStop();
        }

        telemetry.addData("Intake Power: ", robot.intakeLeft.getPower());

    }

    public void takeOut() {
        robot.intakeLeft.setPower(speed);
        robot.intakeRight.setPower(speed);
    }

    public void takeIn() {
        robot.intakeLeft.setPower(-speed);
        robot.intakeRight.setPower(-speed);
    }

    public void fullStop() {
        robot.intakeLeft.setPower(0);
        robot.intakeRight.setPower(0);
    }

    public void speedUp() {
        speed = 1.0;
        isSlow = false;
    }

    public void slowDown() {
        speed = 0.5;
        isSlow = true;
    }
}
