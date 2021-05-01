package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter {
    Hardware robot;
    Telemetry telemetry;
    double flapPos = 0.05;

    public final int shootingRPM = 1700;
    public final double hopperUpPos = 0.14;
    public final double hopperDownPos = 0.0;

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

        if (hopUp && robot.hopper.getPosition() < hopperUpPos) {
            robot.hopper.setPosition(robot.hopper.getPosition() + 0.02);
        } else if (hopDown) {
            hopperDown();
        }

        if (poke && (robot.shooter0.getVelocity() > 0.97 * shootingRPM && robot.shooter0.getVelocity() < 1.03 * shootingRPM)) {
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
        telemetry.addData("Shooter Vel: ", robot.shooter0.getVelocity());
        telemetry.addData("Flap Position: ", robot.flappyFlap.getPosition());
    }

    public void rev(double speed) {
        if (speed > 0) {
            robot.shooter0.setVelocity(1 * shootingRPM);
            robot.shooter1.setVelocity(1 * shootingRPM);
        } else {
            robot.shooter0.setVelocity(0);
            robot.shooter1.setVelocity(0);
        }
    }

    public void hopperDown() {
        robot.hopper.setPosition(0.0);
    }

    public void hopperUp() {
        robot.hopper.setPosition(0.14);
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
        flapPos = 0.048;
        robot.flappyFlap.setPosition(flapPos);
    }

    public void goalAim() {
        flapPos = 0.068;
        robot.flappyFlap.setPosition(flapPos);
    }

    @Deprecated
    public void farGoalAim() {
        flapPos = 0.075;
        robot.flappyFlap.setPosition(flapPos);
    }

    public void longShot() {
        flapPos = 0.069;
        robot.flappyFlap.setPosition(flapPos);
    }

    public void longishShot() {
        flapPos = 0.066;
        robot.flappyFlap.setPosition(flapPos);
    }

    public void longerShot() {
        flapPos = 0.072;
        robot.flappyFlap.setPosition(flapPos);
    }
}
