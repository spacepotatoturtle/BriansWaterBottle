package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Blocker {
    Hardware robot;
    Telemetry telemetry;

    boolean up = false;
    boolean toggleWasPressed = false;

    public Blocker(Hardware hardware, Telemetry telemetryInstance) {
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void blok(boolean block, boolean upToggle) {
        if (upToggle) {
            if (!toggleWasPressed) {
                up = !up;
            }
            toggleWasPressed = true;
        } else {
            toggleWasPressed = false;
        }

        if (block && !up) {
            block();
        } else {
            vertical();
        }
    }

    public void vertical() {
        robot.blocker.setPosition(0.55);
    }

    public void block() {
        robot.blocker.setPosition(0.25);
    }

    public void autoInit() {
        robot.blocker.setPosition(0.96);
    }

}
