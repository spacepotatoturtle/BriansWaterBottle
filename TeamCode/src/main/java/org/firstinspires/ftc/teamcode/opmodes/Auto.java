package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.RoadrunnerDrive;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.Wobbler;

@Autonomous(group = "Performance: Auto")
public class Auto extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Hardware robot = new Hardware();
        Shooter shooter = new Shooter(robot, telemetry);
        Wobbler wobbler = new Wobbler(robot, telemetry);

        robot.init(hardwareMap);
        RoadrunnerDrive drive = new RoadrunnerDrive(hardwareMap);
        shooter.hopperUp();
        shooter.unpoke();

        Pose2d startingPose = new Pose2d(9, -39, 0);
        Pose2d avoidancePose = new Pose2d(9, -80, 0);
        Pose2d powerShotPose = new Pose2d(64, -72.5, 0);
        Pose2d powerShotPose2 = new Pose2d(64, -80, 0);
        Pose2d powerShotPose3 = new Pose2d(64, -65, 0);
        Pose2d zonePose;
        Pose2d collectPose = new Pose2d(36, -36, -Math.PI);
        Pose2d wobblePose = new Pose2d(36, -36, 0);
        Pose2d goalPose = new Pose2d(68, -36, 0);
        Pose2d parkPose = new Pose2d(84, -36, 0);

        drive.setPoseEstimate(startingPose);

        waitForStart();

        wobbler.close();
        shooter.powerShotAim();

        if (isStopRequested()) return;

        int numRings = 0;
        if (numRings == 0) {
            zonePose = new Pose2d(84, -24, 3 * Math.PI / 2);
        } else if (numRings == 1) {
            zonePose = new Pose2d(108, -48, -Math.PI / 2);
        } else {
            zonePose = new Pose2d(132, -24, -Math.PI / 2);
        }

        Trajectory toNotRings = drive.trajectoryBuilder(startingPose)
                .strafeTo(new Vector2d(avoidancePose.getX(), avoidancePose.getY()))
                .build();

        Trajectory toPowerBars = drive.trajectoryBuilder(avoidancePose)
                .splineTo(new Vector2d(powerShotPose.getX(), powerShotPose.getY()), powerShotPose.getHeading())
                .build();

        Trajectory toPowerBars2 = drive.trajectoryBuilder(powerShotPose)
                .strafeTo(new Vector2d(powerShotPose2.getX(), powerShotPose2.getY()))
                .build();

        Trajectory toPowerBars3 = drive.trajectoryBuilder(powerShotPose2)
                .strafeTo(new Vector2d(powerShotPose3.getX(), powerShotPose3.getY()))
                .build();

        // Shooty shooty interval 7.5 inches

        Trajectory toZone0 = drive.trajectoryBuilder(powerShotPose3)
                .splineToLinearHeading(zonePose, -zonePose.getHeading())
                .build();

        // Intake like crazy now

        Trajectory toCollect = drive.trajectoryBuilder(zonePose)
                .splineTo(new Vector2d(collectPose.getX(), collectPose.getY()), collectPose.getHeading())
                .build();

        Trajectory toGoal = drive.trajectoryBuilder(wobblePose)
                .splineTo(new Vector2d(goalPose.getX(), goalPose.getY()), goalPose.getHeading())
                .build();

        // More shooting

        Trajectory toZone1 = drive.trajectoryBuilder(goalPose)
                .splineToLinearHeading(zonePose, zonePose.getHeading())
                .build();

        Trajectory toPark = drive.trajectoryBuilder(zonePose)
                .splineTo(new Vector2d(parkPose.getX(), parkPose.getY()), parkPose.getHeading())
                .build();

        // Start
        drive.followTrajectory(toNotRings);
        drive.update();
        sleep(1000);
        shooter.rev(1);

        // Power shots
        drive.followTrajectory(toPowerBars);
        drive.update();
        shooter.poke();
        sleep(2000);
        shooter.unpoke();
        sleep(1000);
        drive.followTrajectory(toPowerBars2);
        drive.update();
        shooter.poke();
        sleep(2000);
        shooter.unpoke();
        sleep(1000);
        drive.followTrajectory(toPowerBars3);
        drive.update();
        shooter.poke();
        sleep(2000);
        shooter.unpoke();
        sleep(1000);
        shooter.rev(0);

        // N
        drive.followTrajectory(toZone0);
        drive.update();
        sleep(1000);
        wobbler.overWall();
        sleep(100);
        wobbler.open();
        sleep(100);
        drive.followTrajectory(toCollect);
        drive.update();
        sleep(1000);
        drive.turn(Math.PI);
        drive.update();
        sleep(1000);
        wobbler.armDown();
        sleep(100);
        wobbler.close();
        sleep(100);
        wobbler.overWall();
        drive.followTrajectory(toGoal);
        drive.update();
        shooter.rev(1);
        sleep(500);
        int i = 0;
        while (i < 3 && i < numRings) {
            shooter.poke();
            sleep(200);
            shooter.unpoke();
            sleep(200);
            i += 1;
        }
        sleep(500);
        shooter.rev(1);
        drive.followTrajectory(toZone1);
        drive.update();
        sleep(1000);
        wobbler.close();
        sleep(100);
        drive.followTrajectory(toPark);
        sleep(1000);
    }

    public Trajectory spline(RoadrunnerDrive drive, Pose2d start, Pose2d end) {
        return drive.trajectoryBuilder(start)
                .splineTo(new Vector2d(end.getX(), end.getY()), end.getHeading())
                .build();
    }
}
