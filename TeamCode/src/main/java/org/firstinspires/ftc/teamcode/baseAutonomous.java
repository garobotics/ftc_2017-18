package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * Created by ga on 9/13/17.
 */

@Autonomous(name="THE Base Autonomous")

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


    HardwarePushbot robot   = new HardwarePushbot();   // Use a Pushbot's hardware
    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


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
        wheelLB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheelRB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // set drive power to 0
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // METHODS GO HERE: THIS ACTS AS THE MAIN METHOD THAT CALLS ALL THE OTHER METHODS

        driveEncoders(0.5, 10, "forward", 5);
        sleep(1000);
        driveEncoders(0.5, 10, "backward", 5);
        sleep(1000);
        driveEncoders(0.5, 10, "left", 5);
        sleep(1000);
        driveEncoders(0.5, 10, "right", 5);
        sleep(5000);
        knockJewel("blue");
        //TODO: why does the drive encoders method work in isolation but not inside knockJewel method
        end();

    }

    public void end(){
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }


    public void driveEncoders(double speed,
                             double distance, String direction,
                             double timeoutS) {
        int newTargetLB; // in ticks
        int newTargetLF;
        int newTargetRF;
        int newTargetRB;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            // Convert inches to ticks
            int inchesToTicks = (int)(distance * COUNTS_PER_INCH);

            if (direction.equalsIgnoreCase("forward")) { // if direction is forward
                // change target positions to go forward
                newTargetLB = wheelLB.getCurrentPosition() - inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() - inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() - inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() - inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            }
            else if (direction.equalsIgnoreCase("backward")) { // if direction is backward
                // change target positions to go backward
                newTargetLB = wheelLB.getCurrentPosition() + inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() + inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() + inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() + inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            }
            else if (direction.equalsIgnoreCase("left")) { // if direction is left
                // change target positions to go left
                newTargetLB = wheelLB.getCurrentPosition() - inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() + inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() - inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() + inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            }
            else if (direction.equalsIgnoreCase("right")) { // if direction is right
                // change target positions to go right
                newTargetLB = wheelLB.getCurrentPosition() + inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() - inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() + inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() - inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            }
            else {
                stop();
                telemetry.addData("4", String.format("BRO U DIDN'T PASS IN A VALID DIRECTION IM NOT MOVING LOLOL"));
                telemetry.update();
            }

            // Turn On RUN_TO_POSITION
            wheelLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelLB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRB.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // this is the absolute value because setPower works differently with encoders
            wheelLF.setPower(Math.abs(speed));
            wheelRF.setPower(Math.abs(speed));
            wheelRB.setPower(Math.abs(speed));
            wheelLB.setPower(Math.abs(speed));

            // reset the timeout time and start motion.
            runtime.reset();


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (wheelLF.isBusy() && wheelRF.isBusy() && wheelLB.isBusy() && wheelRB.isBusy())) {

                // Display it for the driver.
                /*telemetry.addData("Path1",  "Running to %7d:" , distance); // distance wheels should go
                // show the current position of all four wheels
                telemetry.addData("Path2",  "Currently at %7d, %7d, %7d, %7d",
                        wheelLF.getCurrentPosition(),
                        wheelRF.getCurrentPosition(),
                        wheelRB.getCurrentPosition(),
                        wheelLB.getCurrentPosition());
                telemetry.update(); */
            }

            // Stop all motion
            wheelLF.setPower(0);
            wheelRF.setPower(0);
            wheelRB.setPower(0);
            wheelLB.setPower(0);
/*
            // Turn off RUN_TO_POSITION
            wheelLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            wheelRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
*/
            //  sleep(250);   // optional pause after each move
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
            driveEncoders(0.5, 1, "forward", 1);
            telemetry.addData("4", String.format("I see the opponent's color. Driving forward..."));
        }
        // otherwise, if it's the opposite color, go backward
        else if (!jewelColor.equals("other")) {
            driveEncoders(0.5, 1, "backward", 1);
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
