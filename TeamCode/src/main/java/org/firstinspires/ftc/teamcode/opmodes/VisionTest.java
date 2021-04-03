package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robot.Vision;

@TeleOp(group = "Test: Vision")
public class VisionTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Vision vision = new Vision(telemetry, hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            int numRings = vision.recognize(gamepad1.a);
        }

        sleep(2000);
    }
}
