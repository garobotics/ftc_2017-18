package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by ga on 3/29/17.
 */

public class baseTeleOp extends OpMode {

    // declare motors for wheels
    public DcMotor wheelRF;
    public DcMotor wheelRB;
    public DcMotor wheelLF;
    public DcMotor wheelLB;

    @Override
    public void init() {

        //initialize motors
        wheelRF = hardwareMap.dcMotor.get("rf");
        wheelRB = hardwareMap.dcMotor.get("rb");
        wheelLF = hardwareMap.dcMotor.get("lf");
        wheelLB = hardwareMap.dcMotor.get("lb");

        //initialize servos
        //theBouncer = hardwareMap.servo.get("bouncer");

        //initialize sensors
        //lineSensor = hardwareMap.opticalDistanceSensor.get("lsense");
        //colorSensor = hardwareMap.colorSensor.get("csense");
        //rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rsense");
        //colorSensor.enableLed(false);

        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelLB.setDirection(DcMotor.Direction.REVERSE);

        //TODO: create loop function for basic mechanum drive



    }

}
