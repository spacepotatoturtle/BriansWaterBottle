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

    public void shoot(double shootiness, boolean flapRaise, boolean flapLower, boolean hopUp, boolean hopDown, boolean poke, boolean flapAutoPower, boolean flapAutoGoal) {
        rev(shootiness);

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

        if (flapAutoGoal) {
            goalAim();
        }

        if (flapAutoPower) {
            powerShotAim();
        }

        telemetry.addData("Shooter Power: ", robot.shooter0.getPower());
        telemetry.addData("Flap Position: ", robot.flappyFlap.getPosition());
    }

    public void rev(double speed) {
        robot.shooter0.setPower(speed);
        robot.shooter1.setPower(speed);
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
            flapPos += 0.001;
            robot.flappyFlap.setPosition(flapPos);
        }
    }

    public void flapLower() {
        if (flapPos > 0.0) {
            flapPos -= 0.001;
            robot.flappyFlap.setPosition(flapPos);
        }
    }

    public void powerShotAim() {
        flapPos = 0.035;
        robot.flappyFlap.setPosition(flapPos);
    }

    public void goalAim() {
        flapPos = 0.072;
        robot.flappyFlap.setPosition(flapPos);
    }
}
