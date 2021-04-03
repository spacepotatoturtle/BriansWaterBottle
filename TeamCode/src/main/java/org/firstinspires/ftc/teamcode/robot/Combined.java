package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;

// BUG: Gamepad doesn't like being pointed to.
public class Combined {

    Hardware robot;
    Drive drive;
    Intake intake;
    Wobbler wobbler;
    Shooter shooter;

    Gamepad gamepad1;
    Gamepad gamepad2;

    public Combined(Hardware hardware, Telemetry telemetryInstance, Gamepad gamepad1, Gamepad gamepad2) {

        robot = hardware;
        drive = new Drive(robot, telemetryInstance);
        intake = new Intake(robot, telemetryInstance);
        wobbler = new Wobbler(robot, telemetryInstance);
        shooter = new Shooter(robot, telemetryInstance);

        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

    }

    public void run() {

        drive.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.a);
        intake.intake(gamepad2.right_bumper, gamepad2.left_bumper || (gamepad2.left_trigger != 0 && gamepad2.left_trigger != 1), gamepad2.b);
        shooter.shoot(gamepad2.left_trigger, gamepad2.dpad_up, gamepad2.dpad_down, gamepad2.left_trigger > 0.5 || gamepad2.x, gamepad2.left_trigger < 0.5 || gamepad2.y, gamepad2.right_trigger > 0.5);
        wobbler.wobble(gamepad1.x, gamepad1.dpad_up, gamepad1.dpad_down, gamepad1.dpad_right, gamepad1.a, gamepad1.y);

    }
}
