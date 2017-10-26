package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by ga on 9/13/17.
 */


@TeleOp(name="Base TankBot")

public class baseTeleOp extends OpMode {

    // declare motors for wheels
    public DcMotor wheelRF;
    public DcMotor wheelRB;
    public DcMotor wheelLF;
    public DcMotor wheelLB;
    public Servo jewelArm;
    public Servo bbLeft;
    public Servo bbRight;
    long timerJA = 0;
    long timerBB = 0;

    @Override
    public void init() {

        //initialize motors
        wheelRF = hardwareMap.dcMotor.get("rf");
        wheelRB = hardwareMap.dcMotor.get("rb");
        wheelLF = hardwareMap.dcMotor.get("lf");
        wheelLB = hardwareMap.dcMotor.get("lb");
        jewelArm = hardwareMap.servo.get("jewel");
        bbLeft = hardwareMap.servo.get("BBL");
        bbRight = hardwareMap.servo.get("BBR");

        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLB.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void loop() {

        // set variables to gamepad joysticks
        float yVal = gamepad1.left_stick_y; //left joystick controls all wheels
        float xVal = gamepad1.left_stick_x;
        float spinner = gamepad1.right_stick_x; // x axis of the right joystick
        boolean sideArm = gamepad1.a;
        boolean boardDown = gamepad1.b;



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

        //todo TEST OUT THIS CODE FOR THE JEWEL ARM IT MIGHT NOT WORK
        if(sideArm && timerJA > 75) { // if the a button is pressed
            if (jewelArm.getPosition() == 0) { // if the arm is up
                jewelArm.setPosition(1); // put the arm down
            }
            else {
                jewelArm.setPosition(0); // bring the arm up

            }
            timerJA = 0;
        }
        timerJA++;

        //todo test if these balance board servos work - treat 2 servos as 1
        if(boardDown && timerBB > 75) { // if the b button is pressed
            if (bbRight.getPosition() == 1 && bbLeft.getPosition() == 0) { // if the wheels are up
                bbLeft.setPosition(1); // for left arm 1 is out
                bbRight.setPosition(0); // for right arm 0 is out
            }
            else {
                bbLeft.setPosition(0); // for left arm 0 is in
                bbRight.setPosition(1); // for right arm 1 is in

            }
            timerBB = 0;
        }
        timerBB++;

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



}
