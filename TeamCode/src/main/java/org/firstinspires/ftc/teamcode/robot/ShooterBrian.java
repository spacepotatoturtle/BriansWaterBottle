package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShooterBrian {
    Hardware robot;
    double power = 0.0;

    public ShooterBrian(Hardware hardware) {
        robot = hardware;
    }

    public void shoot(Gamepad gamepad, Telemetry telemetryInstance) {

        power += gamepad.right_trigger * 0.025;
        power -= gamepad.left_trigger * 0.025;

        robot.shooter0.setPower(power);
        robot.shooter1.setPower(power);

        telemetryInstance.addData("Shooter Power: ", robot.shooter0.getPower());
    }
}
