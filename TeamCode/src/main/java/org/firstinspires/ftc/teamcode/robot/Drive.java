package org.firstinspires.ftc.teamcode.robot;

import java.lang.Math;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Class for drive functionality. Called in other classes.
 */

public class Drive {
    Hardware robot;
    Telemetry telemetry;

    float kDrive = 1.0f;
    float kForward = 1.0f;
    float kStrafe = 1.0f;
    float kTurn = 0.8f;

    boolean slowWasPressed = false;
    boolean isSlow = false;

    public Drive(Hardware hardware, Telemetry telemetryInstance) {
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void drive(double forwardness, double strafeness, double turnyness, boolean slowMode) {

        /* Toggle for speed control. The boolean variable compensates for driver slowness.
         */

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

        /* Three components of robot movement: Forwards/Backwards, Left/Right, and Turning.
         */

        double realForwardness = forwardness * kForward;
        double realStrafeness = strafeness * kStrafe;
        double realTurnyness = turnyness * kTurn;


        /* All three components of robot movement are combined into smooth motion in the motors.
         */

        double BL = realForwardness + realStrafeness - realTurnyness;
        double BR = realForwardness - realStrafeness + realTurnyness;
        double FL = realForwardness - realStrafeness - realTurnyness;
        double FR = realForwardness + realStrafeness + realTurnyness;

        /* MAX value ensures the motors are not told to run beyond their peaks while maintaining
        proportions between three components of movement. */

        double MAX = Math.abs(Math.max(Math.max(BL, BR), Math.max(FL, FR)));

        if (MAX > 1) {
            robot.backLeftDrive.setPower(kDrive * BL / MAX);
            robot.backRightDrive.setPower(kDrive * BR / MAX);
            robot.frontLeftDrive.setPower(kDrive * FL / MAX);
            robot.frontRightDrive.setPower(kDrive * FR / MAX);
        } else {
            robot.backLeftDrive.setPower(kDrive * BL);
            robot.backRightDrive.setPower(kDrive * BR);
            robot.frontLeftDrive.setPower(kDrive * FL);
            robot.frontRightDrive.setPower(kDrive * FR);
        }

        /* Telemetry for reference, debugging.
         */

        telemetry.addData("Actual Slow Mode this time: ", isSlow);
        telemetry.addData("Forwardness%3A", realForwardness);
        telemetry.addData("Strafeness%3A", realStrafeness);
        telemetry.addData("Turnyness%3A", realTurnyness);
        telemetry.addData("LEFT REAR", robot.backLeftDrive.getPower());
        telemetry.addData("RIGHT REAR", robot.backRightDrive.getPower());
        telemetry.addData("LEFT FRONT", robot.frontLeftDrive.getPower());
        telemetry.addData("RIGHT FRONT", robot.frontRightDrive.getPower());
    }

    public void speedUp() {
        kDrive = 1.0f;
        //kForward = 1.0f;
        isSlow = false;
    }

    public void slowDown() {
        kDrive = 0.4f;
        //kForward = 0.6f;
        isSlow = true;
    }
}
