package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by ga on 9/13/17.
 */


/* SOLID BLUE LIGHT ON THE EXPANSION HUB MEANS THAT THE PHONE ISN'T CONNECTED*/
/* GREEN LIGHT MEANS IT IS */

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
    public DcMotor liftL;
    public DcMotor liftR;
    public DcMotor glyphLyft;
    public Servo glyphGripRight;
    public Servo glyphGripLeft;
    long timerJA = 0;
    long timerBB = 0;
    //long timerLift = 0;

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
        glyphLyft = hardwareMap.dcMotor.get("glyphlyft");
        glyphGripRight = hardwareMap.servo.get("glyphHolderRight");
        glyphGripLeft = hardwareMap.servo.get("glyphHolderLeft");

        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLB.setDirection(DcMotor.Direction.FORWARD);
        glyphLyft.setDirection(DcMotor.Direction.REVERSE);


    }

    @Override
    public void loop() {

        // set variables to gamepad joysticks
        float yVal = gamepad1.left_stick_y; //left joystick controls all wheels
        float xVal = gamepad1.left_stick_x;
        float spinner = gamepad1.right_stick_x; // x axis of the right joystick
        float cube = gamepad2.right_stick_y; //y axis of the right joystick
        boolean sideArm = gamepad2.y;
        boolean in = gamepad2.x;
        boolean boardDown = gamepad2.b;
        boolean boardUp = gamepad2.a;
        boolean cubeDrop = gamepad2.right_bumper;
        boolean cubeOpen = gamepad2.left_bumper;

        // clip the right/left values so that the values never exceed +/- 1
        yVal = Range.clip(yVal, -1, 1);
        xVal = Range.clip(xVal, -1, 1);
        spinner = Range.clip(spinner, -1, 1);
        cube = Range.clip(cube, -1, 1);

        // scale the joystick value to make it easier to control the robot more precisely at slower speeds.
        yVal = (float) scaleInput(yVal);
        xVal = (float) scaleInput(xVal);
        spinner = (float) scaleInput(spinner);
        cube = (float) scaleInput(cube);

        if (sideArm) {
            jewelArm.setPosition(0);
        }
        if (in) {
            jewelArm.setPosition(0.5);
        }

        //controls glyph lift it literally lifts the cube
        if (cube > 0.5) {
            glyphLyft.setPower(cube / 4);
        } else if (cube < -0.5) {
            glyphLyft.setPower(cube / 2);
        } else {
            glyphLyft.setPower(0);
        }


        if (cubeDrop) {
            glyphGripLeft.setPosition(0.6);
            glyphGripRight.setPosition(0.6);
        }

        if (cubeOpen) {
            glyphGripLeft.setPosition(0.5);
            glyphGripRight.setPosition(0.7);
        }

        // set power to 0 if joysticks are at (0,0)
        if (yVal == 0 && xVal == 0 && spinner == 0) {
            wheelRF.setPower(0.0);
            wheelRB.setPower(0.0);
            wheelLF.setPower(0.0);
            wheelLB.setPower(0.0);
        }

        /* BACKWARDS: all wheels must go forward
           the joystick yVal will be positive when going forward
           in this statement, yVal is negated because the wheels need to go backwards */
        if (yVal > Math.abs(xVal)) {
            wheelRF.setPower(yVal);
            wheelRB.setPower(yVal);
            wheelLF.setPower(yVal);
            wheelLB.setPower(yVal);
        }

        /* FORWARDS: all wheels must go backward
           the joystick yVal will be negative when going backward
           in this statement, yVal is negated because the wheels need to go forwards */
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


        /* SPIN RIGHT: the left wheels go forward and the right wheels go backward
           SPIN LEFT: the right wheels go forward and the left wheels go backward */

        if (spinner != 0) {
            // left wheels forward
            wheelLF.setPower(-spinner);
            wheelLB.setPower(-spinner);
            // right wheels backward
            wheelRF.setPower(spinner);
            wheelRB.setPower(spinner);
        }

        //this code controls the servos that push down the balance board
        // when the b button on gamepad 1 is pressed, the mini arms will go to the opposite position
        // in if out, out if in
        if (boardDown) { // if the b button is pressed
            bbLeft.setPosition(1); // for left arm 1 is out
            bbRight.setPosition(0.0); // for right arm 0 is out

            if (boardUp) { // if the a button is pressed
                bbLeft.setPosition(0.4); // for left arm 0 is in
                bbRight.setPosition(0.6); // for right arm 1 is in
            }
        }

        /*
        front motor should eventually be made into a servo so the position will hold with the
        press of a button, but for now if the button is pressed and while it is being pressed, it
        will either hold the cube or not hold the cube
        */

        /*if(cubeIn){
            while (cubeIn) {
                cubeGrabber.setPower(0.5);
            }
        }
        else if(cubeOut){
            while(cubeOut){
                cubeGrabber.setPower(0.5);
            }
        }
        else{
            cubeGrabber.setPower(0);
        }*/

        }

        @Override
        // stops all motors when opmode is disabled from driver station
        public void stop () {
            wheelRF.setPower(0.0);
            wheelRB.setPower(0.0);
            wheelLF.setPower(0.0);
            wheelLB.setPower(0.0);
            glyphLyft.setPower(0.0);
        }

    /*
	 This method scales the joystick input so for low joystick values, the
	 scaled value is less than linear.  This is to make it easier to drive
	 the robot more precisely at slower speeds.
	 */

    double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

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


