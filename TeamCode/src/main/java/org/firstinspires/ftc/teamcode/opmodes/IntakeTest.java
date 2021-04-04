package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Intake;

@TeleOp(name = "Test: Intake", group = "Test")
public class IntakeTest extends LinearOpMode {
    Hardware robot = new Hardware();
    Intake intake = new Intake(robot, telemetry);

    public void runOpMode() {

        robot.init(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {

            intake.intake(gamepad1.right_bumper, gamepad1.left_bumper, gamepad1.dpad_left);

            telemetry.update();

            sleep(25);
        }
    }
}
