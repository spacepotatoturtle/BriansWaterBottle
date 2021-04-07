package org.firstinspires.ftc.teamcode.robot;

/* Class for drive functionality. Called in other classes.
 */

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Wobbler {
    Hardware robot;
    Telemetry telemetry;

    boolean toggleWasPressed;
    boolean isOpen;

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

    public void armDown() {
        robot.wobbleArm1.setPosition(0.14);
        robot.wobbleArm2.setPosition(0.14);
        robot.wobbleArm3.setPosition(0.14);
        robot.wobbleArm4.setPosition(0.14);
    }

    public void armDownButNotAllTheWay() {
        robot.wobbleArm1.setPosition(0.12);
        robot.wobbleArm2.setPosition(0.12);
        robot.wobbleArm3.setPosition(0.12);
        robot.wobbleArm4.setPosition(0.12);
    }

    public void overWall() {
        robot.wobbleArm1.setPosition(0.22);
        robot.wobbleArm2.setPosition(0.22);
        robot.wobbleArm3.setPosition(0.22);
        robot.wobbleArm4.setPosition(0.22);
    }

    public void initWithoutClose() {
        robot.wobbleArm1.setPosition(0.55);
        robot.wobbleArm2.setPosition(0.55);
        robot.wobbleArm3.setPosition(0.55);
        robot.wobbleArm4.setPosition(0.55);
    }

    public void init() {
        robot.wobbleArm1.setPosition(0.55);
        robot.wobbleArm2.setPosition(0.55);
        robot.wobbleArm3.setPosition(0.55);
        robot.wobbleArm4.setPosition(0.55);
        close();
        isOpen = false;
    }

    public double getArmPosition() {
        return robot.wobbleArm1.getPosition();
    }

    public void close() {
        robot.wobbleClawLeft.setPosition(0.9);
        robot.wobbleClawRight.setPosition(0.18);
        isOpen = false;
    }

    public void open() {
        robot.wobbleClawLeft.setPosition(0.62);
        robot.wobbleClawRight.setPosition(0.48);
        isOpen = true;
    }

    public void wobble(boolean toggleClaw, boolean armRaise, boolean armLower, boolean armInit, boolean armDown, boolean armOverWall) { // The called method.

        if (toggleClaw) {
            if (!toggleWasPressed) {
                if (isOpen) {
                    close();
                } else {
                    open();
                }
            }
            toggleWasPressed = true;
        } else {
            toggleWasPressed = false;
        }

        if (armRaise) {
            robot.wobbleArm1.setPosition(robot.wobbleArm1.getPosition() + 0.015);
            robot.wobbleArm2.setPosition(robot.wobbleArm2.getPosition() + 0.015);
            robot.wobbleArm3.setPosition(robot.wobbleArm3.getPosition() + 0.015);
            robot.wobbleArm4.setPosition(robot.wobbleArm4.getPosition() + 0.015);
        }

        if (armLower && robot.wobbleArm1.getPosition() > 0.1) {
            robot.wobbleArm1.setPosition(robot.wobbleArm1.getPosition() - 0.015);
            robot.wobbleArm2.setPosition(robot.wobbleArm2.getPosition() - 0.015);
            robot.wobbleArm3.setPosition(robot.wobbleArm3.getPosition() - 0.015);
            robot.wobbleArm4.setPosition(robot.wobbleArm4.getPosition() - 0.015);
        }

        if (armInit) {
            init();
        }

        if (armDown) {
            armDown();
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
