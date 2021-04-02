package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.ShooterBrian;

@TeleOp(name = "Test: Shooter Brian", group = "Test")
public class ShooterTestBrian extends LinearOpMode {
    Hardware robot = new Hardware();
    ShooterBrian shooter = new ShooterBrian(robot);

    public void runOpMode() {

        robot.init(hardwareMap);

        waitForStart();

        while(opModeIsActive()) {

            shooter.shoot(gamepad1, telemetry);

            telemetry.update();

            sleep(25);
        }
    }
}
