package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by ga on 9/13/17.
 */

@Autonomous(name="Base Autonomous")

public class baseAutonomous extends LinearOpMode {

    public DcMotor wheelRF;
    public DcMotor wheelRB;
    public DcMotor wheelLF;
    public DcMotor wheelLB;
    public Servo jewelArm;
    public ColorSensor colorSensor;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F,0F,0F};
    String ballColor;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        //INIT STUFF GOES HERE:

        wheelRF = hardwareMap.dcMotor.get("rf");
        wheelRB = hardwareMap.dcMotor.get("rb");
        wheelLF = hardwareMap.dcMotor.get("lf");
        wheelLB = hardwareMap.dcMotor.get("lb");
        jewelArm = hardwareMap.servo.get("jewel");
        colorSensor = hardwareMap.colorSensor.get("sensor_color");


        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLB.setDirection(DcMotor.Direction.FORWARD);


        // reset encoder target positions to 0 on drive wheels
        wheelLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheelRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // set drive power to 0
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // METHODS GO HERE: THIS ACTS AS THE MAIN METHOD THAT CALLS ALL THE OTHER METHODS

        driveEncoders(6000,"FORWARD");
        driveEncoders(6000,"BACKWARD");
        //TODO: why does the drive encoders method work in isolation but not inside knockJewel method
/*
        telemetry.addData("1", String.format("YO IM IN AUTONOMOUS"));
        telemetry.update();
        sleep(1000);
        setJewelArm("out");
        telemetry.addData("1", String.format("YO I PUT MY ARM OUT"));
        telemetry.update();
        sleep(1000);
        knockJewel("red");
        telemetry.update();
        sleep(1000);
        */
        end();

    }

    public void end(){
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }


    public void driveEncoders(int distance, String direction) {
        // options for distance = any integer (this is the target position)
        // options for direction = FORWARD, BACKWARD, CRAB RIGHT, CRAB LEFT

        // reset encoders each time method is called
        wheelLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheelRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        telemetry.addData("1", String.format("YO I RESET THE ENCODERS"));
        telemetry.update();
        sleep(1000);

        // change mode to run to position
        wheelLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wheelRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // set the target position to the parameter passed in


        if (direction.equals("FORWARD")) {

            wheelLF.setTargetPosition(-distance);
            wheelRF.setTargetPosition(-distance);

            wheelLF.setPower(-0.5);
            wheelRF.setPower(-0.5);
            wheelLB.setPower(-0.5);
            wheelRB.setPower(-0.5);


            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() > -distance && wheelRF.getCurrentPosition() > -distance) {
                telemetry.addData("0", String.format("Moving forward, please stand by..."));
                telemetry.addData("1", wheelLF.getCurrentPosition());
                telemetry.addData("2", wheelRF.getCurrentPosition());
                telemetry.update();
            }

        }
        else if (direction.equals("BACKWARD")) {
            wheelLF.setTargetPosition(distance);
            wheelRF.setTargetPosition(distance);

            wheelLF.setPower(0.5);
            wheelRF.setPower(0.5);
            wheelLB.setPower(0.5);
            wheelRB.setPower(0.5);


            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() < distance && wheelRF.getCurrentPosition() < distance) {
                telemetry.addData("1", String.format("Moving backward, please stand by..."));
                telemetry.update();
            }


        }
        else if (direction.equals("CRAB LEFT")) { //
            wheelLF.setTargetPosition(-distance);
            wheelRF.setTargetPosition(distance);

            wheelRF.setPower(0.5);
            wheelRB.setPower(-0.5);
            wheelLF.setPower(-0.5);
            wheelLB.setPower(0.5);

            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() > -distance || wheelRF.getCurrentPosition() < distance) {
                telemetry.addData("2", String.format("Crabbing left, please stand by..."));
                telemetry.update();
            }
        }
        else if (direction.equals("CRAB RIGHT")) {

            wheelLF.setTargetPosition(distance);
            wheelRF.setTargetPosition(-distance);

            wheelRF.setPower(-0.5);
            wheelRB.setPower(0.5);
            wheelLF.setPower(0.5);
            wheelLB.setPower(-0.5);

            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() < distance && wheelRF.getCurrentPosition() > -distance) {
                telemetry.addData("3", String.format("Crabbing right, please stand by..."));
                telemetry.update();
            }
        }
        else {
            wheelLF.setPower(0);
            wheelRF.setPower(0);
            wheelLB.setPower(0);
            wheelRB.setPower(0);
            telemetry.addData("4", String.format("YOU DIDN'T PASS IN A VALID DIRECTION. I'M NOT MOVING. LOLZ"));
            telemetry.update();
        }

    }


    /* @param direction indicates whether we want the jewel arm to go in or out
    * This method allows us to control the jewelArm to knock off the ball */
    public void setJewelArm(String direction) {
        if (direction.equals("out")) { // when @param is "out"
            jewelArm.setPosition(1); // put jewel arm out
            telemetry.addData("4", String.format("Jewel arm is out"));

        }
        else if (direction.equals("in")) { // when @param is "in"
            jewelArm.setPosition(0); // put jewel arm in
            telemetry.addData("4", String.format("Jewel arm is in"));
        }
        else { // when @param is another word
            jewelArm.setPosition(0); // put jewel arm in by default
            telemetry.addData("4", String.format("You didn't pass in a valid direction"));
        }
        telemetry.update();
        sleep(1000);

    }

    /* @param targetColor is the opposite of the team color
       because we want to knock off the other team's jewel */
    public void knockJewel(String targetColor){
        // if the jewel is equal to the targetColor, drive forward to knock it off
        String jewelColor = convertHueToString();
        if (targetColor.equals(jewelColor)){
            driveEncoders(100, "FORWARD");
            telemetry.addData("4", String.format("I see the opponent's color. Driving forward..."));
        }
        // otherwise, if it's the opposite color, go backward
        else if (!jewelColor.equals("other")) {
            driveEncoders(100, "BACKWARD");
            telemetry.addData("4", String.format("I see your team's color. Driving backward..."));
        }
        else { // it's "other"
            stop();
            telemetry.addData("4", String.format("Who turned out the lights? I can't see red or blue."));
        }
        telemetry.update();
        sleep(1000);

    }

    public String convertHueToString() {
        Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);
        if ((hsvValues[0] > -1 && hsvValues[0] < 60) || hsvValues[0] > 300) { // if the hue is red
            ballColor = "red"; // set ball color to red
        }
        else if (hsvValues[0] < 270 && hsvValues[0] > 150) { // if the hue is blue
            ballColor = "blue"; // set ball color to blue
        }
        else {
            ballColor = "other"; // it is seeing another color
        }
        return ballColor;
    }



    public void driveGyro(int degrees) {
        //todo: create driveGyro method that takes in degrees to turn
    }

}
