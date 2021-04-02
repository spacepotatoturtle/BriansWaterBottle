package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter {
    Hardware robot;
    Telemetry telemetry;
    double flapPos = 0.05;

    public Shooter(Hardware hardware, Telemetry telemetryInstance) {
        robot = hardware;
        telemetry = telemetryInstance;
    }

    public void init() {
        robot.flappyFlap.setPosition(0.05);
    }

    public void shoot(double shootiness, boolean flapRaise, boolean flapLower, boolean hopUp, boolean hopDown, boolean poke) {
        robot.shooter0.setPower(shootiness);
        robot.shooter1.setPower(shootiness);

        if (flapRaise) {
            flapRaise();
        } else if (flapLower) {
            flapLower();
        }

        if (hopUp) {
            hopperUp();
        } else if (hopDown) {
            hopperDown();
        }

        if (poke) {
            poke();
        } else {
            unpoke();
        }

        telemetry.addData("Shooter Power: ", robot.shooter0.getPower());
    }

    public void hopperDown() {
        robot.hopper.setPosition(0.0);
    }

    public void hopperUp() {
        robot.hopper.setPosition(0.115);
    }

    public void unpoke() {
        robot.poker.setPosition(0.28);
    }

    public void poke() {
        robot.poker.setPosition(0.0);
    }

    public void flapRaise() {
        if (flapPos < 0.3) {
            flapPos += 0.01;
            robot.flappyFlap.setPosition(flapPos);
        }
    }

    public void flapLower() {
        if (flapPos > 0.0) {
            flapPos -= 0.01;
            robot.flappyFlap.setPosition(flapPos);
        }
    }

    public void aimbot() {

    }
}
