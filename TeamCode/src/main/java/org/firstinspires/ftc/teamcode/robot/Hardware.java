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
    public Servo spin = null;
    public Servo lift1 = null;
    public Servo lift2 = null;

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

        // None of these are actually used
        leftEncoder = new Encoder(hwMap.get(DcMotorEx.class, "intakeLeft"));
        rightEncoder = new Encoder(hwMap.get(DcMotorEx.class, "intakeRight"));
        frontEncoder = new Encoder(hwMap.get(DcMotorEx.class, "leftFront"));

        intakeLeft = hwMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hwMap.get(DcMotor.class, "intakeRight");

        shooter0 = hwMap.get(DcMotor.class, "shooter0");
        shooter1 = hwMap.get(DcMotor.class, "shooter1");

        spin = hwMap.get(Servo.class, "spin");
        lift1 = hwMap.get(Servo.class, "lift1");
        lift2 = hwMap.get(Servo.class, "lift2");

        wobbleClawLeft = hwMap.get(Servo.class, "clawLeft");
        wobbleClawRight = hwMap.get(Servo.class, "clawRight");

        flappyFlap = hwMap.get(Servo.class, "flap");
        hopper = hwMap.get(Servo.class, "hopper");
        poker = hwMap.get(Servo.class, "poker");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        leftEncoder.setDirection(Encoder.Direction.FORWARD);
        rightEncoder.setDirection(Encoder.Direction.REVERSE);
        frontEncoder.setDirection(Encoder.Direction.FORWARD);

        intakeLeft.  setDirection(DcMotor.Direction.FORWARD);
        intakeRight. setDirection(DcMotor.Direction.REVERSE);

        shooter1.setDirection(DcMotorSimple.Direction.REVERSE);

//        wobbleClawLeft.setPosition(0.23);
//        wobbleClawRight.setPosition(0.85);
        wobbleClawLeft.setPosition(0.9);
        wobbleClawRight.setPosition(0.18);
        flappyFlap.setPosition(0.255);
        spin.setPosition(0.32);
        lift1.setPosition(0.25);
        lift2.setPosition(0.25);

    }
}
