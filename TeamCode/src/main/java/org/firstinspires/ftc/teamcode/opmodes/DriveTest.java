package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.robot.Drive;
import org.firstinspires.ftc.teamcode.robot.Hardware;

@TeleOp(name = "Test: Drive", group = "Test")
public class DriveTest extends LinearOpMode {
    Hardware robot = new Hardware();
    Drive drive = new Drive(robot, telemetry);

    public void runOpMode() {

        robot.init(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {

            drive.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.a);

            sleep(25);
        }
    }
}
