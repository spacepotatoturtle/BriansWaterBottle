package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Blocker;
import org.firstinspires.ftc.teamcode.robot.Drive;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.Wobbler;

@TeleOp(name = "Test: Combined", group = "Test")
public class CombinedTest extends LinearOpMode {
    Hardware robot = new Hardware();
    Drive drive = new Drive(robot, telemetry);
    Intake intake = new Intake(robot, telemetry);
    Wobbler wobbler = new Wobbler(robot, telemetry);
    Shooter shooter = new Shooter(robot, telemetry);
    Blocker blocker = new Blocker(robot, telemetry);

    public void runOpMode() {

        robot.init(hardwareMap);
        shooter.goalAim();

        waitForStart();

        while(opModeIsActive()) {

            drive.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.b);
            intake.intake(gamepad2.right_bumper || (gamepad2.left_trigger != 0 && gamepad2.left_trigger != 1), gamepad2.left_bumper, gamepad2.left_trigger > 0);
            shooter.shoot(gamepad2.left_trigger, gamepad2.dpad_right, gamepad2.dpad_left, gamepad2.left_trigger > 0 || gamepad2.x, gamepad2.left_trigger == 0 || gamepad2.y, gamepad2.right_trigger > 0.5, gamepad2.dpad_down, gamepad2.dpad_up);
            wobbler.wobble(gamepad1.x, gamepad1.dpad_up || gamepad1.a, false, gamepad1.dpad_down, gamepad1.dpad_left, gamepad1.dpad_right || gamepad1.a);
            blocker.blok(true, gamepad1.y);

            telemetry.update();

            sleep(25);
        }
    }
}
