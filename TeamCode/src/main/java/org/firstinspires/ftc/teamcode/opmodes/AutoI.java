package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.ContinuationSynchronizer;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.RoadrunnerDrive;
import org.firstinspires.ftc.teamcode.robot.Blocker;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.Intake;
import org.firstinspires.ftc.teamcode.robot.Shooter;
import org.firstinspires.ftc.teamcode.robot.Wobbler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Autonomous(group = "Performance", name = "AutoI")
public class AutoI extends LinearOpMode {

    Hardware robot = new Hardware();
    Shooter shooter = new Shooter(robot, telemetry);
    Wobbler wobbler = new Wobbler(robot, telemetry);
    Intake intake = new Intake(robot, telemetry);
    Blocker blocker = new Blocker(robot, telemetry);

    private static final String TAG = "Webcam Sample";

    /** How long we are to wait to be granted permission to use the camera before giving up. Here,
     * we wait indefinitely */
    private static final int secondsPermissionTimeout = Integer.MAX_VALUE;

    /** State regarding our interaction with the camera */
    private CameraManager cameraManager;
    private WebcamName cameraName;
    private Camera camera;
    private CameraCaptureSession cameraCaptureSession;

    /** The queue into which all frames from the camera are placed as they become available.
     * Frames which are not processed by the OpMode are automatically discarded. */
    private EvictingBlockingQueue<Bitmap> frameQueue;

    /** State regarding where and how to save frames when the 'A' button is pressed. */
    private int captureCounter = 0;
    private File captureDirectory = AppUtil.ROBOT_DATA_DIR;

    /** A utility object that indicates where the asynchronous callbacks from the camera
     * infrastructure are to run. In this OpMode, that's all hidden from you (but see {@link #startCamera}
     * if you're curious): no knowledge of multi-threading is needed here. */
    private Handler callbackHandler;

    @Override
    public void runOpMode() throws InterruptedException {

        // Clockwise is negative

        robot.init(hardwareMap);
        RoadrunnerDrive drive = new RoadrunnerDrive(hardwareMap);
        shooter.hopperUp();
        shooter.unpoke();
        blocker.autoInit();

        robot.shooter0.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(18, 3, 0, 1));

        double powerShotRPM = 1450;
        double[] powerShotAngles = {0, 5 * Math.PI / 180, -10 * Math.PI / 180};


        Pose2d startingPose = new Pose2d(9-72, -39+72, 0);
        Pose2d powerPose = new Pose2d(64-72, -72+72, 0);
        Pose2d powerPoseFinal = new Pose2d(64-72, -72+72, -5 * Math.PI / 180);
        Pose2d pickupPlace1 = new Pose2d(122-72, -35+72, -45 * Math.PI / 180);
        Pose2d pickupPlace2 = new Pose2d(122-72, -82+72, -45 * Math.PI / 180);
        Pose2d wobblePose = new Pose2d(46-72, -32+72, 0);  // 43, -19, Math.PI / 6
        Pose2d wobblePose2 = new Pose2d(35-72, -34+72, 0);  // 37, -24, Math.PI / 6
        Pose2d dodgyWobblePose = new Pose2d(60-72, -64+72, 0);
        Pose2d dodgyWobblePose2 = new Pose2d(25-72, -41+72, 0);
        Pose2d dodgyWobblePose3 = new Pose2d(26-72, -39+72, 0);
        Pose2d shootingPose = new Pose2d(35-72, -38+72, -9 * Math.PI / 180);
        Pose2d shootingPose2 = new Pose2d(46-72, -35+72, -9 * Math.PI / 180);
        Pose2d shootingPose3 = new Pose2d(60-72, -35+72, -9 * Math.PI / 180);
        Pose2d shootingPoseFinal = new Pose2d(64-72, -48+72, 0);
        Pose2d parkPose = new Pose2d(80-72, -36+72, 0);
        Pose2d parkPoseSafe = new Pose2d(77-72, -36+72, 0);

        Pose2d zonePose00Rings = new Pose2d(82-72, -30+72, 0);
        Pose2d zonePose10Rings = new Pose2d(82-72, -32+72, 0);
        Pose2d zonePose01Rings = new Pose2d(108-72, -54+72, 0);
        Pose2d zonePose11Rings = new Pose2d(82-72, -35+72, 3 * Math.PI / 2);
        Pose2d zonePose04Rings = new Pose2d(124-72, -30+72, 0);
        Pose2d zonePose14Rings = new Pose2d(104-72, -16+72, 3 * Math.PI / 2);

        Pose2d shootingPose4Ring = new Pose2d(35-72, -38+72, -11 * Math.PI / 180);
        Pose2d shootingPose24Ring = new Pose2d(46-72, -35+72, -11 * Math.PI / 180);
        Pose2d shootingPose34Ring = new Pose2d(60-72, -35+72, -11 * Math.PI / 180);

        drive.setPoseEstimate(startingPose);

        //CREATE TRAJECTORIES

        telemetry.addData("Status", "Writing trajectories...");
        telemetry.update();

        Trajectory toShooterSpot = drive.trajectoryBuilder(startingPose)
                .splineTo(new Vector2d(powerPose.getX(), powerPose.getY()), powerPose.getHeading(), DriveConstants.PRECISE_SPLINE_SPEED, DriveConstants.NORM_ACCEL)
                .build();

        Trajectory toPickup1 = drive.trajectoryBuilder(powerPoseFinal)
                .splineToLinearHeading(pickupPlace1, pickupPlace1.getHeading())
                .build();

        Trajectory toPickup2 = drive.trajectoryBuilder(pickupPlace1)
                .lineToConstantHeading(pickupPlace2.vec(), DriveConstants.PRECISE_SPLINE_SPEED, DriveConstants.NORM_ACCEL)
                .build();

        Trajectory toZone00Rings = drive.trajectoryBuilder(pickupPlace2, true)
                .splineToSplineHeading(zonePose00Rings, -Math.PI)
                .addTemporalMarker(1.5, () -> wobbler.armMiddle())
                .addTemporalMarker(1.5, () -> wobbler.armSide())
                .splineToSplineHeading(wobblePose, wobblePose.getHeading())
                .addSpatialMarker(zonePose00Rings.vec(), () -> wobbler.open())
                .addSpatialMarker(zonePose00Rings.vec(), () -> intake.intakeSpeed(0.5))
                .build();

        Trajectory toZone01Rings = drive.trajectoryBuilder(pickupPlace2, true)
                .splineToSplineHeading(zonePose01Rings, -Math.PI, DriveConstants.PRECISE_SPLINE_SPEED, DriveConstants.NORM_ACCEL)
                .addTemporalMarker(0, () -> wobbler.armMiddle())
                .addTemporalMarker(0, () -> wobbler.armSide())
                .splineTo(dodgyWobblePose.vec(), -Math.PI)
                .splineToConstantHeading(dodgyWobblePose2.vec(), Math.PI / 2)
                .addSpatialMarker(zonePose01Rings.vec(), () -> wobbler.open())
                .addSpatialMarker(zonePose01Rings.vec(), () -> intake.intakeSpeed(0.5))
                .addSpatialMarker(dodgyWobblePose.vec(), () -> wobbler.armDown())
                .build();

        /*
        Trajectory toZone04Rings = drive.trajectoryBuilder(pickupPlace2, true)
                .splineToSplineHeading(zonePose04Rings, -Math.PI)
                .addTemporalMarker(1.5, () -> wobbler.armMiddle())
                .addTemporalMarker(1.5, () -> wobbler.armSide())
                .splineToSplineHeading(dodgyWobblePose, dodgyWobblePose.getHeading())
                .addSpatialMarker(zonePose04Rings.vec(), () -> wobbler.open())
                .addSpatialMarker(zonePose04Rings.vec(), () -> intake.intakeSpeed(0.5))
                .build();
        */

        Trajectory toWobble2 = drive.trajectoryBuilder(wobblePose)
                .strafeTo(wobblePose2.vec())
                .build();

        Trajectory toDodgyWobble = drive.trajectoryBuilder(dodgyWobblePose2)
                .strafeTo(dodgyWobblePose3.vec())
                .build();

        // For 0 rings
        Trajectory toShootingGoal0Rings = drive.trajectoryBuilder(wobblePose2)
                .splineTo(shootingPoseFinal.vec(), shootingPoseFinal.getHeading())
                .addTemporalMarker(0.5, () -> robot.hopper.setPosition(0.14))
                .build();

        // For 1 ring
        Trajectory toShootingGoal01Ring = drive.trajectoryBuilder(wobblePose2)
                .splineToLinearHeading(shootingPose, 0)
                .addTemporalMarker(0.1, () -> robot.hopper.setPosition(0.14))
                .build();

        Trajectory toShootingGoal11Ring = drive.trajectoryBuilder(shootingPose)
                .splineToLinearHeading(shootingPoseFinal, 0)
                .addTemporalMarker(0, () -> intake.takeIn())
                .addTemporalMarker(0, () -> shooter.hopperDown())
                .build();

        Trajectory toZone10Rings = drive.trajectoryBuilder(shootingPoseFinal)
                .splineToLinearHeading(zonePose10Rings, 0)
                .build();

        Trajectory toZone11Rings = drive.trajectoryBuilder(shootingPoseFinal)
                .splineToLinearHeading(zonePose11Rings, 0)
                .build();

        /*
        Trajectory toZone14Rings = drive.trajectoryBuilder(shootingPoseFinal)
                .splineToLinearHeading(zonePose14Rings, 0)
                .build();
        */

        //stuff just for 4 rings
        Trajectory toShooterSpot4Rings = drive.trajectoryBuilder(startingPose)
                .splineTo(shootingPose4Ring.vec(), shootingPose4Ring.getHeading(), DriveConstants.KINDA_SLOW, DriveConstants.NORM_ACCEL)
                .build();

        Trajectory toShooterSpot24Rings = drive.trajectoryBuilder(shootingPose4Ring)
                .addTemporalMarker(0.1, () -> intake.intakeSpeed(1))
                .strafeTo(shootingPose24Ring.vec(), DriveConstants.KINDA_SLOW, DriveConstants.NORM_ACCEL)
                .build();

        Trajectory toShooterSpot34Rings = drive.trajectoryBuilder(shootingPose24Ring)
                .addTemporalMarker(0.1, () -> intake.intakeSpeed(1))
                .splineToLinearHeading(powerPose, -Math.PI / 2, DriveConstants.SLOW, DriveConstants.NORM_ACCEL)
                .build();

        Trajectory toZone04Rings = drive.trajectoryBuilder(powerPoseFinal)
                .splineToLinearHeading(zonePose04Rings, -zonePose04Rings.getHeading())
                .build();

        Trajectory toWobble24Rings = drive.trajectoryBuilder(wobblePose)
                .strafeTo(wobblePose2.vec())
                .build();

        Trajectory toZone14Rings = drive.trajectoryBuilder(wobblePose2)
                .splineToLinearHeading(zonePose14Rings, 0)
                .build();

        Trajectory toPark4Rings = drive.trajectoryBuilder(zonePose14Rings)
                .strafeTo(parkPose.vec())
                .build();

        Trajectory toPark1Rings = drive.trajectoryBuilder(zonePose11Rings)
                .strafeTo(parkPoseSafe.vec())
                .build();


        Trajectory toZone0 = null;
        Trajectory toZone1 = null;

        telemetry.addData("Status", "Booting up webcam...");
        telemetry.update();

        callbackHandler = CallbackLooper.getDefault().getHandler();

        cameraManager = ClassFactory.getInstance().getCameraManager();
        cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        initializeFrameQueue(2);
        AppUtil.getInstance().ensureDirectoryExists(captureDirectory);

        openCamera();
        if (camera == null) return;

        startCamera();
        if (cameraCaptureSession == null) return;

        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();

        waitForStart();

        int numRings = 0;

        Bitmap bmp = frameQueue.poll();
        if (bmp != null) {
            // 640x480 Resolution.
            // Approx. 192, 121, 79 Ring Color.
            // Approx. 135, 140, 139 Floor Color.
            int[] pixels = new int[640 * 480];
            bmp.getPixels(pixels, 0, 640, 0, 0, 640, 480);
//            telemetry.addData("Pixel 0,0,R: ", (bmp.getPixel(320, 240) >> 16) & 0xff);
//            telemetry.addData("Pixel 0,0,G: ", (bmp.getPixel(320, 240) >> 8) & 0xff);
//            telemetry.addData("Pixel 0,0,B: ", (bmp.getPixel(320, 240)) & 0xff);
//            telemetry.addData("Scan Output: ", scan(bmp));
//            onNewFrame(bmp);

            telemetry.addData("Status", "Beginning calculation...");

            int maxWidth = 0;
            int width = 0;
            boolean prev = false;
            int i = 0;
            while (i < 480 * 640) {

                // First check if new row
                // If so, pretend like last pixel did not meet criteria
                if (i % 640 == 0) {
                    prev = false;
                    if (maxWidth < width) {
                        maxWidth = width;
                    }
                }

                if (((pixels[i] >> 16) & 0xff) > 1.3 * ((pixels[i] >> 8) & 0xff) &&
                        ((pixels[i] >> 8) & 0xff) > 1.3 * (pixels[i] & 0xff)) {
                    if (prev) {
                        width += 1;
                    } else {
                        width = 1;
                    }
                    prev = true;
                } else {
                    prev = false;
                    if (maxWidth < width) {
                        maxWidth = width;
                    }
                }
                i += 1;
            }

            int maxHeight = 0;
            int height = 0;
            prev = false;
            i = 640;
            while (i != 0) {

                // First check if new column
                // If so, pretend like last pixel did not meet criteria
                if (i < 640) {
                    prev = false;
                    if (maxHeight < height) {
                        maxHeight = height;
                    }
                }

                if (((pixels[i] >> 16) & 0xff) > 1.3 * ((pixels[i] >> 8) & 0xff) &&
                        ((pixels[i] >> 8) & 0xff) > 1.3 * (pixels[i] & 0xff)) {
                    if (prev) {
                        height += 1;
                    } else {
                        height = 1;
                    }
                    prev = true;
                } else {
                    if (maxHeight < height) {
                        maxHeight = height;
                    }
                }
                i = (i + 640) % (480 * 640 - 1);
            }

            telemetry.addData("Max Width: ", maxWidth);
            telemetry.addData("Max Height: ", maxHeight);

            double ratio = (double) maxHeight / maxWidth * 5 / 0.75;

            if (maxWidth > 80) {
                if (ratio > 4) {
                    telemetry.addData("Num of rings: ", 4);
                    numRings = 4;
                } else {
                    telemetry.addData("Num of rings: ", 1);
                    numRings = 1;
                }
            } else {
                telemetry.addData("Num of rings: ", 0);
                numRings = 0;
            }
        }

        telemetry.addData("Status", "Closing camera...");
        telemetry.update();

        closeCamera();

        telemetry.addData("Status", "Running...");
        telemetry.update();

        if (numRings == 4) {
            sleep(600);
        }

        wobbler.close();
        shooter.powerShotAim();

        if (isStopRequested()) return;

        if (numRings == 0) {
            toZone0 = toZone00Rings;
            toZone1 = toZone10Rings;
        } else if (numRings == 1) {
            toZone0 = toZone01Rings;
            toZone1 = toZone11Rings;
        } else {
            toZone0 = toZone04Rings;
            toZone1 = toZone14Rings;
        }

        if (numRings != 4) {

            // High shots
            wobbler.armVertical();
            shooter.autoRev(powerShotRPM);
            drive.followTrajectory(toShooterSpot);
            drive.update();
            //drive.turn(initialPowerAngle);
            drive.update();

            shooter.powerShotAimAuto();

            for (int i = 0; i < 3; i++) {

                drive.turn(powerShotAngles[i]);
                drive.update();

                int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                while ((robot.shooter0.getVelocity() < 0.97 * powerShotRPM || robot.shooter0.getVelocity() > 1.03 * powerShotRPM) && (county < 20)) {
                    sleep(25);
                    county++;
                }
                shooter.poke();
                sleep(250);
                shooter.unpoke();

            }
            blocker.vertical();

            shooter.rev(0);
            intake.fullStop();
            shooter.hopperDown();

            drive.followTrajectory(toPickup1);
            drive.update();
            intake.intakeSpeed(1);

            drive.followTrajectory(toPickup2);
            drive.update();
            //intake.intakeSpeed(0.5);

            // Drop Wobble
            drive.followTrajectory(toZone0);
            drive.update();


            // Pick up second Wobble
        /*
        Trajectory toWobble = drive.trajectoryBuilder(zonePose)
                .strafeTo(wobblePose.vec())
                .build();
        drive.followTrajectory(toWobble);
        drive.update();
         */

            if (numRings == 0) {

                wobbler.armDown();
                wobbler.armBack();
                sleep(200);
                drive.followTrajectory(toWobble2);
                drive.update();
                intake.fullStop();

            } else {

                wobbler.armDown();
                wobbler.armSide();
                sleep(200);
                drive.followTrajectory(toDodgyWobble);
                drive.update();
                intake.fullStop();

            }

            wobbler.close();
            sleep(350);
            wobbler.armVertical();

//        shooter.hopperUp();

            while (robot.hopper.getPosition() < 0.14) {
                robot.hopper.setPosition(robot.hopper.getPosition() + 0.02);
                sleep(25);
            }

            double tempPower = 1700;
            shooter.autoRev(tempPower);

            if (numRings == 0) {

                shooter.goalAim();

                drive.followTrajectory(toShootingGoal0Rings);
                drive.update();

                for (int i = 0; i < 3; i++) {

                    int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                    while ((robot.shooter0.getVelocity() < 0.97 * tempPower || robot.shooter0.getVelocity() > 1.03 * tempPower) && (county < 20)) {
                        sleep(25);
                        county++;
                    }
                    shooter.poke();
                    sleep(250);
                    shooter.unpoke();

                }

            } else if (numRings == 1) {

                robot.flappyFlap.setPosition(0.073);

                drive.followTrajectory(toShootingGoal01Ring);
                drive.update();

                for (int i = 0; i < 3; i++) {

                    int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                    while ((robot.shooter0.getVelocity() < 0.97 * tempPower || robot.shooter0.getVelocity() > 1.03 * tempPower) && (county < 20)) {
                        sleep(25);
                        county++;
                    }
                    shooter.poke();
                    sleep(250);
                    shooter.unpoke();

                }

                drive.followTrajectory(toShootingGoal11Ring);
                drive.update();

                shooter.goalAim();
                shooter.hopperUp();
                sleep(300);

                for (int i = 0; i < 2; i++) {

                    int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                    while ((robot.shooter0.getVelocity() < 0.97 * tempPower || robot.shooter0.getVelocity() > 1.03 * tempPower) && (county < 20)) {
                        sleep(25);
                        county++;
                    }
                    shooter.poke();
                    sleep(250);
                    shooter.unpoke();

                }

            }

            shooter.rev(0);

            // Drop second Wobble
            drive.followTrajectory(toZone1);
            drive.update();
            wobbler.armSide();
            sleep(250);
            wobbler.armMiddle();
            sleep(750);
            wobbler.open();
            sleep(250);

            // Park
            wobbler.armBack();
            wobbler.armVertical();

            if (numRings == 1) {
                drive.followTrajectory(toPark1Rings);
                drive.update();
            }


        /*
        if (numRings != 0) {
            drive.followTrajectory(toPark);
            drive.update();
        } else {
            Trajectory toPark0 = drive.trajectoryBuilder(zonePose2)
                    .strafeTo(new Vector2d(zonePose2.getX(), parkPose.getY()))
                    .build();
            Trajectory toPark1 = drive.trajectoryBuilder(new Pose2d(zonePose2.getX(), parkPose.getY(), zonePose2.getHeading()))
                    .strafeTo(parkPose.vec())
                    .build();
            drive.followTrajectory(toPark0);
            drive.update();
            drive.followTrajectory(toPark1);
            drive.update();
        }

         */

        } else {


            // High shots
            wobbler.armVertical();
            shooter.rev(1);
            drive.followTrajectory(toShooterSpot4Rings);
            drive.update();
            shooter.longerShot();

            for (int i = 0; i < 3; i++) {
                int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                while ((robot.shooter0.getVelocity() < 0.97 * shooter.shootingRPM || robot.shooter0.getVelocity() > 1.03 * shooter.shootingRPM) && (county < 20)) {
                    sleep(25);
                    county++;
                }
                telemetry.addData("AAA", robot.shooter0.getVelocity());
                telemetry.update();
                shooter.poke();
                sleep(250);
                shooter.unpoke();
            }

            blocker.vertical();

            //shoots one ring first
            //setting up intaking
            shooter.longishShot();
//            intake.intakeSpeed(1); //this now happens as a marker in the trajectory
            shooter.hopperDown();

            //goes to second shooting spot
            drive.followTrajectory(toShooterSpot24Rings);
            drive.update();

            //stops intake before lifting hopper
            sleep(250);
            intake.intakeSpeed(0);
            sleep(400);
            while (robot.hopper.getPosition() < 0.14) {
                robot.hopper.setPosition(robot.hopper.getPosition() + 0.02);
                sleep(25);
            }

            //shoots
            for (int i = 0; i < 1; i++) {
                int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                while ((robot.shooter0.getVelocity() < 0.97 * shooter.shootingRPM || robot.shooter0.getVelocity() > 1.03 * shooter.shootingRPM) && (county < 20)) {
                    sleep(25);
                    county++;
                }
                telemetry.addData("AAA", robot.shooter0.getVelocity());
                telemetry.update();
                shooter.poke();
                sleep(250);
                shooter.unpoke();
            }

            //lowers hopper then revs up the intake again
            shooter.hopperDown();
            sleep(250);

            //power shots now


            shooter.autoRev(powerShotRPM);
            drive.followTrajectory(toShooterSpot34Rings);
            drive.update();

            while (robot.hopper.getPosition() < 0.14) {
                robot.hopper.setPosition(robot.hopper.getPosition() + 0.02);
                sleep(25);
            }
            //drive.turn(initialPowerAngle);

            shooter.powerShotAimAuto();
//            while (robot.hopper.getPosition() < 0.14) {
//                robot.hopper.setPosition(robot.hopper.getPosition() + 0.02);
//                sleep(25);
//            }

            sleep(200);

            for (int i = 0; i < 3; i++) {

                drive.turn(powerShotAngles[i]);
                drive.update();

                int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                while ((robot.shooter0.getVelocity() < 0.97 * powerShotRPM || robot.shooter0.getVelocity() > 1.03 * powerShotRPM) && (county < 20)) {
                    sleep(25);
                    county++;
                }
                shooter.poke();
                sleep(250);
                shooter.unpoke();

            }

            /*
            shooter.longShot();
            //intake.intakeSpeed(1); //this now happens as a marker in the trajectory

            //drive to location then stop intake and raise the hopper
            drive.followTrajectory(toShooterSpot34Rings);
            drive.update();

            sleep(250);
            intake.intakeSpeed(0);
            sleep(400);
            shooter.hopperUp();
            sleep(300);

            //shoots the rings
            for (int i = 0; i < 3; i++) {
                int county = 0; //tries to rev up for 0.5 seconds before just giving up and shooting
                while ((robot.shooter0.getVelocity() < 0.97 * shooter.shootingRPM || robot.shooter0.getVelocity() > 1.03 * shooter.shootingRPM) && (county < 20)) {
                    sleep(25);
                    county++;
                }
                telemetry.addData("AAA", robot.shooter0.getVelocity());
                telemetry.update();
                shooter.poke();
                sleep(250);
                shooter.unpoke();
            }

             */

            shooter.rev(0);
            intake.fullStop();

            // Drop Wobble
            wobbler.armSide();
            wobbler.armMiddle();
            drive.followTrajectory(toZone04Rings);
            drive.update();
            sleep(100);
            wobbler.open();
            sleep(100);
            wobbler.armVertical();
            wobbler.armBack();

            // Pick up second Wobble
            Trajectory toWobble = drive.trajectoryBuilder(zonePose04Rings)
                    .strafeTo(wobblePose.vec())
                    .build();
            drive.followTrajectory(toWobble);
            drive.update();
            wobbler.armDown();
            sleep(200);
            drive.followTrajectory(toWobble24Rings);
            drive.update();
            wobbler.close();
            sleep(350);
            wobbler.armVertical();

            // Drop second Wobble
            drive.followTrajectory(toZone14Rings);
            drive.update();
            wobbler.armSide();
            sleep(250);
            wobbler.armMiddle();
            sleep(750);
            wobbler.open();
            sleep(250);

            // Park
            wobbler.armBack();
            wobbler.armVertical();
            blocker.block();

            drive.followTrajectory(toPark4Rings);
            drive.update();

        }

        blocker.block();

    }

    public void slowWobble(Hardware robot, double position, double time) {
        /*
        double inc = (position - robot.wobbleArm1.getPosition()) / time * 0.025;
        double t = 0;
        while (t < time) {
            robot.wobbleArm1.setPosition(robot.wobbleArm1.getPosition() + inc);
            robot.wobbleArm2.setPosition(robot.wobbleArm2.getPosition() + inc);
            robot.wobbleArm3.setPosition(robot.wobbleArm3.getPosition() + inc);
            robot.wobbleArm4.setPosition(robot.wobbleArm4.getPosition() + inc);
            t += 0.025;
            sleep(25);
        }

         */
    }

    private int scan(Bitmap frame) {
        List<Integer> lengths = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < 480; i += 1) {
            if (((frame.getPixel(320, i) >> 16) & 0xff) > 160) {
                if (!flag) {
                    lengths.add(1);
                } else {
                    lengths.set(lengths.size() - 1, lengths.get(lengths.size() - 1) + 1);
                }
                flag = true;
            } else {
                flag = false;
            }
        }
        int max = 0;
        for (int i = 0; i < lengths.size(); i += 1) {
            if (lengths.get(i) > max) {
                max = lengths.get(i);
            }
        }
        return max;
    }

    /** Do something with the frame */
    private void onNewFrame(Bitmap frame) {
        saveBitmap(frame);
        frame.recycle(); // not strictly necessary, but helpful
    }

    //----------------------------------------------------------------------------------------------
    // Camera operations
    //----------------------------------------------------------------------------------------------

    private void initializeFrameQueue(int capacity) {
        /** The frame queue will automatically throw away bitmap frames if they are not processed
         * quickly by the OpMode. This avoids a buildup of frames in memory */
        frameQueue = new EvictingBlockingQueue<Bitmap>(new ArrayBlockingQueue<Bitmap>(capacity));
        frameQueue.setEvictAction(new Consumer<Bitmap>() {
            @Override public void accept(Bitmap frame) {
                // RobotLog.ii(TAG, "frame recycled w/o processing");
                frame.recycle(); // not strictly necessary, but helpful
            }
        });
    }

    private void openCamera() {
        if (camera != null) return; // be idempotent

        Deadline deadline = new Deadline(secondsPermissionTimeout, TimeUnit.SECONDS);
        camera = cameraManager.requestPermissionAndOpenCamera(deadline, cameraName, null);
        if (camera == null) {
            error("camera not found or permission to use not granted: %s", cameraName);
        }
    }

    private void startCamera() {
        if (cameraCaptureSession != null) return; // be idempotent

        /** YUY2 is supported by all Webcams, per the USB Webcam standard: See "USB Device Class Definition
         * for Video Devices: Uncompressed Payload, Table 2-1". Further, often this is the *only*
         * image format supported by a camera */
        final int imageFormat = ImageFormat.YUY2;

        /** Verify that the image is supported, and fetch size and desired frame rate if so */
        CameraCharacteristics cameraCharacteristics = cameraName.getCameraCharacteristics();
        if (!contains(cameraCharacteristics.getAndroidFormats(), imageFormat)) {
            error("image format not supported");
            return;
        }
        final Size size = cameraCharacteristics.getDefaultSize(imageFormat);
        final int fps = cameraCharacteristics.getMaxFramesPerSecond(imageFormat, size);

        /** Some of the logic below runs asynchronously on other threads. Use of the synchronizer
         * here allows us to wait in this method until all that asynchrony completes before returning. */
        final ContinuationSynchronizer<CameraCaptureSession> synchronizer = new ContinuationSynchronizer<>();
        try {
            /** Create a session in which requests to capture frames can be made */
            camera.createCaptureSession(Continuation.create(callbackHandler, new CameraCaptureSession.StateCallbackDefault() {
                @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        /** The session is ready to go. Start requesting frames */
                        final CameraCaptureRequest captureRequest = camera.createCaptureRequest(imageFormat, size, fps);
                        session.startCapture(captureRequest,
                                new CameraCaptureSession.CaptureCallback() {
                                    @Override public void onNewFrame(@NonNull CameraCaptureSession session, @NonNull CameraCaptureRequest request, @NonNull CameraFrame cameraFrame) {
                                        /** A new frame is available. The frame data has <em>not</em> been copied for us, and we can only access it
                                         * for the duration of the callback. So we copy here manually. */
                                        Bitmap bmp = captureRequest.createEmptyBitmap();
                                        cameraFrame.copyToBitmap(bmp);
                                        frameQueue.offer(bmp);
                                    }
                                },
                                Continuation.create(callbackHandler, new CameraCaptureSession.StatusCallback() {
                                    @Override public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, CameraCaptureSequenceId cameraCaptureSequenceId, long lastFrameNumber) {
                                        RobotLog.ii(TAG, "capture sequence %s reports completed: lastFrame=%d", cameraCaptureSequenceId, lastFrameNumber);
                                    }
                                })
                        );
                        synchronizer.finish(session);
                    } catch (CameraException |RuntimeException e) {
                        RobotLog.ee(TAG, e, "exception starting capture");
                        error("exception starting capture");
                        session.close();
                        synchronizer.finish(null);
                    }
                }
            }));
        } catch (CameraException|RuntimeException e) {
            RobotLog.ee(TAG, e, "exception starting camera");
            error("exception starting camera");
            synchronizer.finish(null);
        }

        /** Wait for all the asynchrony to complete */
        try {
            synchronizer.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        /** Retrieve the created session. This will be null on error. */
        cameraCaptureSession = synchronizer.getValue();
    }

    private void stopCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.stopCapture();
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private void closeCamera() {
        stopCamera();
        if (camera != null) {
            camera.close();
            camera = null;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------------------------

    private void error(String msg) {
        telemetry.log().add(msg);
        telemetry.update();
    }
    private void error(String format, Object...args) {
        telemetry.log().add(format, args);
        telemetry.update();
    }

    private boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) return true;
        }
        return false;
    }

    private void saveBitmap(Bitmap bitmap) {
        File file = new File(captureDirectory, String.format(Locale.getDefault(), "webcam-frame-%d.jpg", captureCounter++));
        try {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                telemetry.log().add("captured %s", file.getName());
            }
        } catch (IOException e) {
            RobotLog.ee(TAG, e, "exception in saveBitmap()");
            error("exception saving %s", file.getName());
        }
    }
}
