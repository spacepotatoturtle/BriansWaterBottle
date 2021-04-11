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
        isOpen = false;
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void armRaise() {
        robot.lift1.setPosition(robot.lift1.getPosition() - 0.01);
        robot.lift2.setPosition(robot.lift2.getPosition() - 0.01);
    }

    public void armLower() {
        robot.lift1.setPosition(robot.lift1.getPosition() + 0.01);
        robot.lift2.setPosition(robot.lift2.getPosition() + 0.01);
    }

    public void armLefter() {
        robot.spin.setPosition(robot.spin.getPosition() - 0.01);
    }

    public void armRighter() {
        robot.spin.setPosition(robot.spin.getPosition() + 0.01);
    }

    public void armSide() {
        robot.spin.setPosition(0.66);
    }

    public void armBack() {
        robot.spin.setPosition(0.32);
    }

    public void armDown() {
        robot.lift1.setPosition(0.95);
        robot.lift2.setPosition(0.95);
    }

    public void armUp() {
        robot.lift1.setPosition(0.85);
        robot.lift2.setPosition(0.85);
    }

    public void armVertical() {
        robot.lift1.setPosition(0.5);
        robot.lift2.setPosition(0.5);
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

    public void wobble(boolean toggleClaw, boolean armVertical, boolean armUp, boolean armDown, boolean armSide, boolean armBack) {

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

        if (armVertical) {
            armVertical();
        }

        if (armUp) {
            armUp();
        }

        if (armDown) {
            armDown();
        }

        if (armSide) {
            armSide();
        }

        if (armBack) {
            armBack();
        }

        //telemetry.addData("Left Claw Pos: ", robot.wobbleClawLeft.getPosition());
//        telemetry.addData("Right Claw Pos: ", robot.wobbleClawRight.getPosition());
        telemetry.addData("Arm Phi: ", robot.lift1.getPosition());
        telemetry.addData("Arm Theta: ", robot.spin.getPosition());

    }
}
