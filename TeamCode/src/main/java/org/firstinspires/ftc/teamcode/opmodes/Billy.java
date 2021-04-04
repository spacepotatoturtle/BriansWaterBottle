package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Hardware;

@TeleOp(name = "Test: Billy", group = "Test")
public class Billy extends LinearOpMode {
    Hardware robot = new Hardware();

    public void runOpMode() {

        robot.init(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {

            if (gamepad1.a) {
                robot.backRightDrive.setPower(1);
            } else {
                robot.backRightDrive.setPower(0);
            }


            if (gamepad1.x) {
                robot.backLeftDrive.setPower(1);
            } else {
                robot.backLeftDrive.setPower(0);
            }


            if (gamepad1.y) {
                robot.frontLeftDrive.setPower(1);
            } else {
                robot.frontLeftDrive.setPower(0);
            }


            if (gamepad1.b) {
                robot.frontRightDrive.setPower(1);
            } else {
                robot.frontRightDrive.setPower(0);
            }

            telemetry.update();

            sleep(25);
        }
    }
}
