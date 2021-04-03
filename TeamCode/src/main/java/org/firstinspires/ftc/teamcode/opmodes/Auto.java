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

        Pose2d startingPose = new Pose2d(9, 39, 0);
        Pose2d avoidancePose = new Pose2d(24, 60, 0);
        Pose2d powerShotPose = new Pose2d(72, 60, 0);
        Pose2d zonePose;
        Pose2d collectPose = new Pose2d(36, 36, -Math.PI);
        Pose2d wobblePose = new Pose2d(36, 36, 0);
        Pose2d goalPose = new Pose2d(72, 36, 0);
        Pose2d parkPose = new Pose2d(84, 36, 0);

        drive.setPoseEstimate(startingPose);

        waitForStart();

        if (isStopRequested()) return;

        int numRings = 0;
        if (numRings == 0) {
            zonePose = new Pose2d(132, 24, -Math.PI / 2);
        } else if (numRings == 1) {
            zonePose = new Pose2d(108, 48, -Math.PI / 2);
        } else {
            zonePose = new Pose2d(84, 24, -Math.PI / 2);
        }

        Trajectory toNotRings = drive.trajectoryBuilder(startingPose)
                .splineTo(avoidancePose.vec(), avoidancePose.getHeading())
                .build();

        Trajectory toPowerBars = drive.trajectoryBuilder(avoidancePose)
                .splineTo(powerShotPose.vec(), powerShotPose.getHeading())
                .build();

        // Shooty shooty interval 7.5 inches

        Trajectory toZone0 = drive.trajectoryBuilder(powerShotPose)
                .splineTo(zonePose.vec(), zonePose.getHeading())
                .build();

        // Intake like crazy now

        Trajectory toCollect = drive.trajectoryBuilder(zonePose)
                .splineTo(collectPose.vec(), collectPose.getHeading())
                .build();

        Trajectory toWobble = drive.trajectoryBuilder(collectPose)
                .splineTo(wobblePose.vec(), wobblePose.getHeading())
                .build();

        Trajectory toGoal = drive.trajectoryBuilder(wobblePose)
                .splineTo(goalPose.vec(), goalPose.getHeading())
                .build();

        // More shooting

        Trajectory toZone1 = drive.trajectoryBuilder(goalPose)
                .splineTo(zonePose.vec(), zonePose.getHeading())
                .build();

        Trajectory toPark = drive.trajectoryBuilder(zonePose)
                .splineTo(parkPose.vec(), parkPose.getHeading())
                .build();

        drive.followTrajectory(toNotRings);
        drive.followTrajectory(toPowerBars);
        drive.followTrajectory(toZone0);
        drive.followTrajectory(toCollect);
        drive.followTrajectory(toWobble);
        drive.followTrajectory(toGoal);
        drive.followTrajectory(toZone1);
        drive.followTrajectory(toPark);

        sleep(2000);
    }
}
