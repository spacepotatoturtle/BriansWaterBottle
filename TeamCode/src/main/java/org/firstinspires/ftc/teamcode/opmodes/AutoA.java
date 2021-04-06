package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.apache.commons.math3.geometry.Vector;
import org.firstinspires.ftc.teamcode.drive.RoadrunnerDrive;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.Wobbler;

@Autonomous(group = "Performance", name = "AutoA")
public class AutoA extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // Clockwise is negative

        Hardware robot = new Hardware();
        Shooter shooter = new Shooter(robot, telemetry);
        Wobbler wobbler = new Wobbler(robot, telemetry);
        Intake intake = new Intake(robot, telemetry);

        robot.init(hardwareMap);
        RoadrunnerDrive drive = new RoadrunnerDrive(hardwareMap);
        shooter.hopperUp();
        shooter.unpoke();

        Pose2d startingPose = new Pose2d(9, -39, 0);
        Pose2d avoidancePose = new Pose2d(9, -72.5, 0);
        Pose2d powerShotPose = new Pose2d(64, -72.5, 0);
        Pose2d powerShotPose2 = new Pose2d(64, -80, 0);
        Pose2d powerShotPose3 = new Pose2d(64, -65, 0);
        Pose2d zonePose;
        Pose2d wobblePose = new Pose2d(47, -17, 8 * Math.PI / 48);
        Pose2d wobblePose2 = new Pose2d(42, -19, 8 * Math.PI / 48);
        Pose2d interPose = new Pose2d(42, -19, 0);
        Pose2d backPose = new Pose2d(12, -36, 0);
        Pose2d goalPose = new Pose2d(36, -36, 0);
        Pose2d zonePose2;
        Pose2d parkPose = new Pose2d(84, -36, 0);

        drive.setPoseEstimate(startingPose);

        waitForStart();

        wobbler.close();
        shooter.powerShotAim();

        if (isStopRequested()) return;

        int numRings = 0;
        if (numRings == 0) {
            zonePose = new Pose2d(81, -37, 3 * Math.PI / 2);
            zonePose2 = new Pose2d(76, -37, 3 * Math.PI / 2);
        } else if (numRings == 1) {
            zonePose = new Pose2d(105, -61, -Math.PI / 2);
            zonePose2 = new Pose2d(100, -37, 3 * Math.PI / 2);
        } else {
            zonePose = new Pose2d(129, -37, -Math.PI / 2);
            zonePose2 = new Pose2d(124, -37, 3 * Math.PI / 2);
        }

        Trajectory trajectory = drive.trajectoryBuilder(startingPose)
                .addTemporalMarker(0, () -> shooter.rev(1))
                .splineTo(powerShotPose.vec(), powerShotPose.getHeading())
                .addTemporalMarker(3, () -> shooter.poke())
                .addTemporalMarker(3.75, () -> shooter.unpoke())
                .splineToConstantHeading(powerShotPose2.vec(), -Math.PI / 2)
//                .addTemporalMarker(3.25, () -> shooter.poke())
//                .addTemporalMarker(4, () -> shooter.unpoke())
                .build();

//        Trajectory toPowerBars2 = drive.trajectoryBuilder(powerShotPose)
//                .strafeTo(new Vector2d(powerShotPose2.getX(), powerShotPose2.getY()))
//                .build();
//
//        Trajectory toPowerBars3 = drive.trajectoryBuilder(powerShotPose2)
//                .strafeTo(new Vector2d(powerShotPose3.getX(), powerShotPose3.getY()))
//                .build();
//
//        // Shooty shooty interval 7.5 inches
//
//        Trajectory toZone0 = drive.trajectoryBuilder(powerShotPose3)
//                .splineToLinearHeading(zonePose, -zonePose.getHeading())
//                .build();
//
//        // Intake like crazy now
//
//        Trajectory toWobble = drive.trajectoryBuilder(zonePose)
//                .strafeTo(new Vector2d(wobblePose.getX(), wobblePose.getY()))
//                .build();
//
//        Trajectory toWobble2 = drive.trajectoryBuilder(wobblePose)
//                .strafeTo(new Vector2d(wobblePose2.getX(), wobblePose2.getY()))
//                .build();
//
//        Trajectory toBack = drive.trajectoryBuilder(interPose)
//                .strafeTo(backPose.vec())
//                .build();
//
//        Trajectory toGoal = drive.trajectoryBuilder(backPose)
//                .splineTo(new Vector2d(goalPose.getX(), goalPose.getY()), goalPose.getHeading())
//                .build();
//
//        // More shooting
//
//        Trajectory toZone1 = drive.trajectoryBuilder(goalPose)
//                .splineToLinearHeading(zonePose2, zonePose2.getHeading())
//                .build();
//
//        Trajectory toPark = drive.trajectoryBuilder(zonePose2)
//                .splineTo(new Vector2d(parkPose.getX(), parkPose.getY()), parkPose.getHeading())
//                .build();
//
//        // Power shots
////        shooter.rev(1);
//        drive.followTrajectory(toPowerBarsDirect);
//        drive.update();
////        shooter.poke();
//        sleep(750);
////        shooter.unpoke();
//        drive.followTrajectory(toPowerBars2);
//        drive.update();
////        shooter.poke();
//        sleep(750);
////        shooter.unpoke();
//        drive.followTrajectory(toPowerBars3);
//        drive.update();
////        shooter.poke();
//        sleep(750);
////        shooter.unpoke();
////        shooter.rev(0);
//        wobbler.armDownButNotAllTheWay();
//        sleep(100);
//
//        // Drop Wobble
//        drive.followTrajectory(toZone0);
//        drive.update();
////        sleep(1300);
////        robot.wobbleClawLeft.setPosition(0.46);
////        robot.wobbleClawRight.setPosition(0.44);
//        wobbler.open();
//        wobbler.initWithoutClose();
////        sleep(500);
////        wobbler.open();
////        wobbler.initWithoutClose();
//
//        // Pick up second Wobble
//        drive.followTrajectory(toWobble);
//        drive.update();
//        drive.turn(2 * Math.PI + wobblePose.getHeading() - zonePose.getHeading());
//        drive.update();
////        wobbler.armDown();
////        wobbler.open();
//        sleep(500);
//        drive.followTrajectory(toWobble2);
//        drive.update();
////        wobbler.close();
//        sleep(500);
////        wobbler.initWithoutClose();
//        sleep(500);
//
//        // Collect and shoot at goal
//        drive.turn(backPose.getHeading() - wobblePose2.getHeading());
//        drive.followTrajectory(toBack);
//        drive.update();
////        shooter.hopperDown();
////        if (numRings != 0) {
////            intake.takeIn();
////        }
//        drive.followTrajectory(toGoal);
//        drive.update();
////        if (numRings != 0) {
////            sleep(500);
////            intake.fullStop();
////            shooter.rev(1);
////            sleep(500);
////            int i = 0;
////            while (i < 3 && i < numRings) {
////                shooter.poke();
////                sleep(750);
////                shooter.unpoke();
////                sleep(750);
////                i += 1;
////            }
//        sleep(500);
////            shooter.rev(0);
////        }
//
//        // Drop second Wobble
//        drive.followTrajectory(toZone1);
//        drive.update();
//        sleep(1000);
////        wobbler.armDownButNotAllTheWay();
//        sleep(750);
////        wobbler.open();
//        sleep(250);
////        drive.followTrajectory(toPark);
////        sleep(1000);

        drive.followTrajectory(trajectory);
    }

    public Trajectory spline(RoadrunnerDrive drive, Pose2d start, Pose2d end) {
        return drive.trajectoryBuilder(start)
                .splineTo(new Vector2d(end.getX(), end.getY()), end.getHeading())
                .build();
    }
}
