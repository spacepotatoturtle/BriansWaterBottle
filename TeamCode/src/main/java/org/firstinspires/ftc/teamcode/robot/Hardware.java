package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.Encoder;

public class Hardware {
    public DcMotor frontLeftDrive = null;
    public DcMotor frontRightDrive = null;
    public DcMotor backLeftDrive = null;
    public DcMotor backRightDrive = null;

    public DcMotor intakeLeft = null;
    public DcMotor intakeRight = null;

    public Servo wobbleClawLeft = null;
    public Servo wobbleClawRight = null;
    public Servo wobbleArm1 = null;
    public Servo wobbleArm2 = null;
    public Servo wobbleArm3 = null;
    public Servo wobbleArm4 = null;

    public Encoder leftEncoder = null;
    public Encoder rightEncoder = null;
    public Encoder frontEncoder = null;

    public DcMotor shooter0 = null;
    public DcMotor shooter1 = null;

    public Servo flappyFlap = null;
    public Servo hopper = null;
    public Servo poker = null;

    HardwareMap hwMap = null;

    public Hardware()
    {
        // Constructor
    }

    public void init(HardwareMap ahwMap) {

        hwMap = ahwMap;

        // At convenience, change names and config of RL and RR to BL and BR, respectively.

        frontLeftDrive = hwMap.get(DcMotor.class, "leftFront");  //left odometry encoder
        frontRightDrive = hwMap.get(DcMotor.class, "rightFront");  //right odometry encoder
        backLeftDrive = hwMap.get(DcMotor.class, "leftRear");  //back/front odometry encoder
        backRightDrive = hwMap.get(DcMotor.class, "rightRear");

        leftEncoder = new Encoder(hwMap.get(DcMotorEx.class, "intakeLeft"));
        rightEncoder = new Encoder(hwMap.get(DcMotorEx.class, "intakeRight"));
        frontEncoder = new Encoder(hwMap.get(DcMotorEx.class, "shooter0"));

        intakeLeft = hwMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hwMap.get(DcMotor.class, "intakeRight");

        shooter0 = hwMap.get(DcMotor.class, "shooter0");
        shooter1 = hwMap.get(DcMotor.class, "shooter1");

        // 1-4, front-back
        wobbleArm1 = hwMap.get(Servo.class, "arm1");
        wobbleArm2 = hwMap.get(Servo.class, "arm2");
        wobbleArm3 = hwMap.get(Servo.class, "arm3");
        wobbleArm4 = hwMap.get(Servo.class, "arm4");

        wobbleClawLeft = hwMap.get(Servo.class, "clawLeft");
        wobbleClawRight = hwMap.get(Servo.class, "clawRight");

        flappyFlap = hwMap.get(Servo.class, "flap");
        hopper = hwMap.get(Servo.class, "hopper");
        poker = hwMap.get(Servo.class, "poker");

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        leftEncoder.setDirection(Encoder.Direction.FORWARD);
        rightEncoder.setDirection(Encoder.Direction.REVERSE);
        frontEncoder.setDirection(Encoder.Direction.FORWARD);

        intakeLeft.  setDirection(DcMotor.Direction.FORWARD);
        intakeRight. setDirection(DcMotor.Direction.REVERSE);

        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);

        wobbleArm2.setDirection(Servo.Direction.REVERSE);
        wobbleArm4.setDirection(Servo.Direction.REVERSE);

        wobbleClawLeft.setPosition(0);
        wobbleClawRight.setPosition(0.85);
        flappyFlap.setPosition(0.255);
        wobbleArm1.setPosition(0.52);
        wobbleArm2.setPosition(0.52);
        wobbleArm3.setPosition(0.52);
        wobbleArm4.setPosition(0.52);
    }
}
