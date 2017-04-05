package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

//todo: see if we need to import Hardware Class


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

        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelLB.setDirection(DcMotor.Direction.REVERSE);

    }

    //todo: create loop function for basic mechanum drive

    @Override
    public void loop() {

        // set variables to gamepad joysticks
        float yVal = gamepad1.left_stick_y; //left joystick controls all wheels
        float xVal = gamepad1.left_stick_x;
        float spinner = gamepad1.right_stick_x; // x axis of the right joystick

        // negate all values since the y axis is reversed on the joypads and -1 should be 1
        yVal = -(yVal);

        // clip the right/left values so that the values never exceed +/- 1
        yVal = Range.clip(yVal, -1, 1);
        xVal = Range.clip(xVal, -1, 1);
        spinner = Range.clip(spinner, -1, 1);

        // scale the joystick value to make it easier to control the robot more precisely at slower speeds.
        yVal = (float) scaleInput(yVal);
        xVal = (float) scaleInput(xVal);
        spinner = (float) scaleInput(spinner);


        // set power to 0 if joysticks are at (0,0)
        if (yVal == 0 && xVal == 0 && spinner == 0) {
            wheelRF.setPower(0.0);
            wheelRB.setPower(0.0);
            wheelLF.setPower(0.0);
            wheelLB.setPower(0.0);
        }

        // BACKWARDS: all wheels must go forward
        // the joystick yVal will be positive when going forward
        // in this statement, yVal is negated because the wheels need to go backwards
        if (yVal > Math.abs(xVal)) {
            wheelRF.setPower(yVal);
            wheelRB.setPower(yVal);
            wheelLF.setPower(yVal);
            wheelLB.setPower(yVal);
        }

        // FORWARDS: all wheels must go backward
        // the joystick yVal will be negative when going backward
        // in this statement, yVal is negated because the wheels need to go forwards
        if (yVal < Math.abs(xVal) && Math.abs(yVal) > Math.abs(xVal)) {
            wheelRF.setPower(yVal * 0.5);
            wheelRB.setPower(yVal * 0.5);
            wheelLF.setPower(yVal * 0.5);
            wheelLB.setPower(yVal * 0.5);
        }

        /* RIGHT: the right front and left rear wheels must go forward
           and the right rear and left front wheels must go backward */
        if (xVal > Math.abs(yVal)) {
            wheelRF.setPower(xVal); // 0 < xVal < 1
            wheelRB.setPower(-xVal); // -1 < -xVal < 0
            wheelLF.setPower(-xVal);
            wheelLB.setPower(xVal);
        }

        /* LEFT: the right rear and left front wheels must go forward
           and the right front and left rear wheels must go backward */
        if (xVal < Math.abs(yVal) && Math.abs(xVal) > Math.abs(yVal)) {
            wheelRF.setPower(xVal); // -1 < xVal < 0
            wheelRB.setPower(-xVal); // 0 < -xVal < 1
            wheelLF.setPower(-xVal);
            wheelLB.setPower(xVal);
        }


        // SPIN RIGHT: the left wheels go forward and the right wheels go backward
        // SPIN LEFT: the right wheels go forward and the left wheels go backward

        if (spinner != 0) {
            // left wheels forward
            wheelLF.setPower(-spinner);
            wheelLB.setPower(-spinner);
            // right wheels backward
            wheelRF.setPower(spinner);
            wheelRB.setPower(spinner);
        }
        else {
        }
    }

    @Override
    // stops all motors when opmode is disabled from driver station
    public void stop() {
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }

    /*
	 This method scales the joystick input so for low joystick values, the
	 scaled value is less than linear.  This is to make it easier to drive
	 the robot more precisely at slower speeds.
	 */

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }

    //todo: edit this file or build out more functionality

}
