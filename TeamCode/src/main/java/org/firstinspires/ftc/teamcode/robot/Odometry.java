package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.Encoder;

public class Odometry {

    Hardware robot;

    private final float lateralDistance = 15.75f;
    private final float forwardOffset = 2.0f;

    private final float wheelRadius = 0.69f;
    private final float gearRatio = 1.0f;
    private final float ticksPerRev = 8192.0f;

    private float[] encoderPosPast = {0.0f, 0.0f, 0.0f};
    private float[] position = {0.0f, 0.0f, 0.0f};

    public Odometry(Hardware hardware) {

        robot = hardware;

    }

    public void init() {
        encoderPosPast[0] = (float) robot.leftEncoder.getCurrentPosition();
        encoderPosPast[1] = (float) robot.frontEncoder.getCurrentPosition();
        encoderPosPast[2] = (float) robot.rightEncoder.getCurrentPosition();
    }

    public void updatePosition() {

        float[] encoderChange = new float[3];

        encoderChange[0] = encoderTicksToInches(robot.leftEncoder.getCurrentPosition() - encoderPosPast[0]);
        encoderChange[1] = encoderTicksToInches(robot.frontEncoder.getCurrentPosition() - encoderPosPast[1]);
        encoderChange[2] = encoderTicksToInches(robot.rightEncoder.getCurrentPosition() - encoderPosPast[2]);

        float[] posChange = new float[3];
        float[] posChangeLoc = new float[3];

        posChangeLoc[0] = (encoderChange[0] + encoderChange[2]) / 2.0f;
        posChangeLoc[1] = encoderChange[1] - forwardOffset * (encoderChange[0] - encoderChange[2]) / lateralDistance;
        posChangeLoc[2] = (encoderChange[0] - encoderChange[2]) / lateralDistance;

        posChange[0] = posChangeLoc[0] * (float) Math.cos(position[2]) - posChangeLoc[1] * (float) Math.sin(position[2]);
        posChange[1] = posChangeLoc[0] * (float) Math.sin(position[2]) + posChangeLoc[1] * (float) Math.cos(position[2]);
        posChange[2] = posChangeLoc[2];

        position[0] += posChange[0];
        position[1] += posChange[1];
        position[2] += posChange[2];
        position[2] = (position[2] + 2 * (float) Math.PI) % (2 * (float) Math.PI);

        encoderPosPast[0] = (float) robot.leftEncoder.getCurrentPosition();
        encoderPosPast[1] = (float) robot.frontEncoder.getCurrentPosition();
        encoderPosPast[2] = (float) robot.rightEncoder.getCurrentPosition();

    }

    public float encoderTicksToInches(double ticks) {
        return (float) (wheelRadius * 2 * Math.PI * gearRatio * ticks / ticksPerRev);
    }

    public float getX() {
        return position[0];
    }

    public float getY() {
        return position[1];
    }

    public float getW() {
        return position[2];
    }

}
