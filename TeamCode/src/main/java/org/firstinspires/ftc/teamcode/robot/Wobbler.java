package org.firstinspires.ftc.teamcode.robot;

/* Class for drive functionality. Called in other classes.
 */

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Wobbler {
    Hardware robot;
    Telemetry telemetry;

    boolean toggleWasPressed;
    boolean isClosed;

    public Wobbler(Hardware hardware, Telemetry telemetryInstance) {
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void zerofy() {
        robot.wobbleArm1.setPosition(0);
        robot.wobbleArm2.setPosition(0);
        robot.wobbleArm3.setPosition(0);
        robot.wobbleArm4.setPosition(0);
    }

    public void pickUp() {
        robot.wobbleArm1.setPosition(0.22);
        robot.wobbleArm2.setPosition(0.22);
        robot.wobbleArm3.setPosition(0.22);
        robot.wobbleArm4.setPosition(0.22);
    }

    public void overWall() {
        robot.wobbleArm1.setPosition(0.04);
        robot.wobbleArm2.setPosition(0.04);
        robot.wobbleArm3.setPosition(0.04);
        robot.wobbleArm4.setPosition(0.04);
    }

    public void init() {
        robot.wobbleArm1.setPosition(0.55);
        robot.wobbleArm2.setPosition(0.55);
        robot.wobbleArm3.setPosition(0.55);
        robot.wobbleArm4.setPosition(0.55);
        open();
        isClosed = false;
    }

    public double getArmPosition() {
        return robot.wobbleArm1.getPosition();
    }

    public void open() {
        robot.wobbleClawLeft.setPosition(0.42);
        robot.wobbleClawRight.setPosition(0.16);
        isClosed = false;
    }

    public void close() {
        robot.wobbleClawLeft.setPosition(0.16);
        robot.wobbleClawRight.setPosition(0.44);
        isClosed = true;
    }

    public void wobble(boolean toggleClaw, boolean armRaise, boolean armLower, boolean armInit, boolean armPickUp, boolean armOverWall) { // The called method.

        if (toggleClaw) {
            if (!toggleWasPressed) {
                if (isClosed) {
                    open();
                } else {
                    close();
                }
            }
            toggleWasPressed = true;
        } else {
            toggleWasPressed = false;
        }

        if (armRaise) {
            robot.wobbleArm1.setPosition(robot.wobbleArm1.getPosition() + 0.01);
            robot.wobbleArm2.setPosition(robot.wobbleArm2.getPosition() + 0.01);
            robot.wobbleArm3.setPosition(robot.wobbleArm3.getPosition() + 0.01);
            robot.wobbleArm4.setPosition(robot.wobbleArm4.getPosition() + 0.01);
        }

        if (armLower) {
            robot.wobbleArm1.setPosition(robot.wobbleArm1.getPosition() - 0.01);
            robot.wobbleArm2.setPosition(robot.wobbleArm2.getPosition() - 0.01);
            robot.wobbleArm3.setPosition(robot.wobbleArm3.getPosition() - 0.01);
            robot.wobbleArm4.setPosition(robot.wobbleArm4.getPosition() - 0.01);
        }

        if (armInit) {
            init();
        }

        if (armPickUp) {
            pickUp();
        }

        if (armOverWall) {
            overWall();
        }

        telemetry.addData("Left Claw Pos: ", robot.wobbleClawLeft.getPosition());
        telemetry.addData("Right Claw Pos: ", robot.wobbleClawRight.getPosition());
        telemetry.addData("Arm Pos: ", getArmPosition());
        telemetry.addData("Arm Pos 2: ", robot.wobbleArm2.getPosition());

    }
}
