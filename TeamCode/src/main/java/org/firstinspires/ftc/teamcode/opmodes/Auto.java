package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.apache.commons.math3.geometry.Vector;
import org.firstinspires.ftc.teamcode.drive.RoadrunnerDrive;

@Autonomous(group = "Performance: Auto")
public class Auto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        RoadrunnerDrive drive = new RoadrunnerDrive(hardwareMap);

        Pose2d startingPose = new Pose2d(9, -39, 0);
        Pose2d avoidancePose = new Pose2d(24, -60, 0);
        Pose2d powerShotPose = new Pose2d(72, -60, 0);
        Pose2d zonePose;
        Pose2d collectPose = new Pose2d(36, -36, -Math.PI);
        Pose2d wobblePose = new Pose2d(36, -36, 0);
        Pose2d goalPose = new Pose2d(72, -36, 0);
        Pose2d parkPose = new Pose2d(84, -36, 0);

        drive.setPoseEstimate(startingPose);

        waitForStart();

        if (isStopRequested()) return;

        int numRings = 0;
        if (numRings == 0) {
            zonePose = new Pose2d(84, 24, -Math.PI / 2);
        } else if (numRings == 1) {
            zonePose = new Pose2d(108, 48, -Math.PI / 2);
        } else {
            zonePose = new Pose2d(132, 24, -Math.PI / 2);
        }

        Trajectory toNotRings = drive.trajectoryBuilder(startingPose)
                .splineTo(new Vector2d(avoidancePose.getX(), avoidancePose.getY()), avoidancePose.getHeading())
                .build();

        Trajectory toPowerBars = drive.trajectoryBuilder(avoidancePose)
                .splineTo(new Vector2d(powerShotPose.getX(), powerShotPose.getY()), powerShotPose.getHeading())
                .build();

        // Shooty shooty interval 7.5 inches

        Trajectory toZone0 = drive.trajectoryBuilder(powerShotPose)
                .splineToLinearHeading(zonePose, zonePose.getHeading())
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

        drive.followTrajectory(toNotRings);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toPowerBars);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toZone0);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toCollect);
        drive.update();
        sleep(2000);
        drive.turn(Math.PI);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toGoal);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toZone1);
        drive.update();
        sleep(2000);
        drive.followTrajectory(toPark);

        sleep(2000);
    }

    public Trajectory spline(RoadrunnerDrive drive, Pose2d start, Pose2d end) {
        return drive.trajectoryBuilder(start)
                .splineTo(new Vector2d(end.getX(), end.getY()), end.getHeading())
                .build();
    }
}
