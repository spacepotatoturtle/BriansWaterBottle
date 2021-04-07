package org.firstinspires.ftc.teamcode.opmodes;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
import org.firstinspires.ftc.teamcode.drive.RoadrunnerDrive;
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

@Autonomous(group = "Performance", name = "AutoB")
public class AutoB extends LinearOpMode {

    Hardware robot = new Hardware();
    Shooter shooter = new Shooter(robot, telemetry);
    Wobbler wobbler = new Wobbler(robot, telemetry);
    Intake intake = new Intake(robot, telemetry);

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

        Pose2d startingPose = new Pose2d(9, -39, 0);
        Pose2d shootingPose = new Pose2d(35, -38, -9 * Math.PI / 180);
        Pose2d shootingPose2 = new Pose2d(41, -34, -9 * Math.PI / 180);
        Pose2d shootingPose3 = new Pose2d(56, -34, -9 * Math.PI / 180);
        Pose2d zonePose;
        Pose2d wobblePose = new Pose2d(47, -17, 8 * Math.PI / 48);
        Pose2d wobblePose2 = new Pose2d(41, -22, 8 * Math.PI / 48);
        Pose2d zonePose2;
        Pose2d parkPose = new Pose2d(84, -36, 0);

        drive.setPoseEstimate(startingPose);

        callbackHandler = CallbackLooper.getDefault().getHandler();

        cameraManager = ClassFactory.getInstance().getCameraManager();
        cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        initializeFrameQueue(2);
        AppUtil.getInstance().ensureDirectoryExists(captureDirectory);

        openCamera();
        if (camera == null) return;

        startCamera();
        if (cameraCaptureSession == null) return;

        waitForStart();

        int numRings = 0;

        Bitmap bmp = frameQueue.poll();
        if (bmp != null) {
            // 640x480 Resolution.
            // Approx. 192, 121, 79 Ring Color.
            // Approx. 135, 140, 139 Floor Color.
            int[] pixels = new int[640 * 480];
            bmp.getPixels(pixels, 0, 640, 0, 0, 640, 480);
            telemetry.addData("Pixel 0,0,R: ", (bmp.getPixel(320, 240) >> 16) & 0xff);
            telemetry.addData("Pixel 0,0,G: ", (bmp.getPixel(320, 240) >> 8) & 0xff);
            telemetry.addData("Pixel 0,0,B: ", (bmp.getPixel(320, 240)) & 0xff);
            telemetry.addData("Scan Output: ", scan(bmp));
            onNewFrame(bmp);

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

        closeCamera();

        telemetry.update();

        wobbler.close();
        shooter.powerShotAim();

        if (isStopRequested()) return;

        if (numRings == 0) {
            zonePose = new Pose2d(81, -37, 3 * Math.PI / 2);
            zonePose2 = new Pose2d(73, -37, 3 * Math.PI / 2);
        } else if (numRings == 1) {
            zonePose = new Pose2d(102, -59, 3 * Math.PI / 2);
            zonePose2 = new Pose2d(92, -59, 3 * Math.PI / 2);
        } else {
            zonePose = new Pose2d(124, -35, 3 * Math.PI / 2);
            zonePose2 = new Pose2d(114, -35, 3 * Math.PI / 2);
        }

        Trajectory toShooterSpot = drive.trajectoryBuilder(startingPose)
                .splineTo(new Vector2d(shootingPose.getX(), shootingPose.getY()), shootingPose.getHeading())
                .build();

        Trajectory toShooterSpot2 = drive.trajectoryBuilder(shootingPose)
                .strafeTo(new Vector2d(shootingPose2.getX(), shootingPose2.getY()))
                .build();

        Trajectory toShooterSpot3 = drive.trajectoryBuilder(shootingPose2)
                .strafeTo(new Vector2d(shootingPose3.getX(), shootingPose3.getY()))
                .build();

        // Shooty shooty interval 7.5 inches

        Trajectory toZone0 = drive.trajectoryBuilder(shootingPose3)
                .splineToLinearHeading(zonePose, -zonePose.getHeading())
                .build();

        Trajectory toWobble = drive.trajectoryBuilder(zonePose)
                .splineToLinearHeading(wobblePose, -Math.PI)
                .build();

        Trajectory toWobble2 = drive.trajectoryBuilder(wobblePose)
                .strafeTo(new Vector2d(wobblePose2.getX(), wobblePose2.getY()))
                .build();

        Trajectory toZone1 = drive.trajectoryBuilder(wobblePose2)
                .splineToLinearHeading(zonePose2, 0)
                .build();

        Trajectory toPark = drive.trajectoryBuilder(zonePose2)
                .splineToLinearHeading(parkPose, -Math.PI)
                .build();

        // high shots
        shooter.rev(1);
        drive.followTrajectory(toShooterSpot);
        drive.update();
        shooter.longerShot();
        for (int i = 0; i < 3; i++) {
            sleep(250);
            shooter.poke();
            sleep(250);
            shooter.unpoke();
        }
        if (numRings > 0) {
            shooter.longShot();
            intake.intakeSpeed(0.8);
            shooter.hopperDown();
        }
        drive.followTrajectory(toShooterSpot2);
        drive.update();
        if (numRings > 0) {
            sleep(650);
            shooter.hopperUp();
            sleep(300);
            for (int i = 0; i < 2; i++) {
                shooter.hopperUp();
                sleep(250);
                shooter.poke();
                sleep(250);
                shooter.unpoke();
            }
            shooter.hopperDown();
        }
        drive.followTrajectory(toShooterSpot3);
        drive.update();
        if (numRings > 1) {
            sleep(650);
            shooter.hopperUp();
            sleep(300);
            for (int i = 0; i < 2; i++) {
                sleep(250);
                shooter.poke();
                sleep(250);
                shooter.unpoke();
            }
        }

        shooter.rev(0);
        intake.fullStop();

        // Drop Wobble
        drive.followTrajectory(toZone0);
        drive.update();
        slowWobble(robot, 0.12, 0.85);
//        wobbler.armDownButNotAllTheWay();
        sleep(100);
        wobbler.open();
        slowWobble(robot, 0.55, 0.65);
//        wobbler.initWithoutClose();

        // Pick up second Wobble
        drive.followTrajectory(toWobble);
        drive.update();
        slowWobble(robot, 0.13, 0.6);
//        wobbler.armDown();
        drive.followTrajectory(toWobble2);
        drive.update();
        wobbler.close();
        sleep(350);
//        wobbler.initWithoutClose();
        slowWobble(robot, 0.55, 0.85);

        // Drop second Wobble
        drive.followTrajectory(toZone1);
        drive.update();
//        wobbler.armDownButNotAllTheWay();
        slowWobble(robot, 0.12, 0.85);
        sleep(100);
        wobbler.open();
        sleep(250);

        // Park
        slowWobble(robot, 0.55, 0.5);
        drive.followTrajectory(toPark);
        drive.update();
        drive.turn(Math.PI / 2);
        drive.update();


    }

    public void slowWobble(Hardware robot, double position, double time) {
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
