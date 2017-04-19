package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by ga on 4/5/17.
 */

@Autonomous(name="Base Autonomous")


public class baseAutonomous extends LinearOpMode {
    //todo: make a working autonomous program lol

    public DcMotor wheelRF;
    public DcMotor wheelRB;
    public DcMotor wheelLF;
    public DcMotor wheelLB;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        //INIT STUFF GOES HERE:

        wheelRF = hardwareMap.dcMotor.get("rf");
        wheelRB = hardwareMap.dcMotor.get("rb");
        wheelLF = hardwareMap.dcMotor.get("lf");
        wheelLB = hardwareMap.dcMotor.get("lb");


        //set directions of motors when driving
        wheelRF.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLB.setDirection(DcMotor.Direction.FORWARD);


        // reset encoder target positions to 0 on drive wheels and ball flipper
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
        driveEncoders(5000, "FORWARD");
        driveEncoders(5000, "BACKWARD");
        driveEncoders(5000, "CRAB RIGHT");
        driveEncoders(5000, "CRAB LEFT");

        //todo: the roboto moves forward for a little and then stops - we don't know why

        // put other methods from below that we want to call here
        end();

    }

    public void end(){
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }

    //todo: add more functionality


    public void driveEncoders(int distance, String direction) {
        // options for distance = any integer (this is the target position)
        // options for direction = FORWARD, BACKWARD, CRAB RIGHT, CRAB LEFT

        // reset encoders each time method is called
        wheelLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheelRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // change mode to run to position
        wheelLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wheelRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // set the target position to the parameter passed in
        wheelLF.setTargetPosition(distance);
        wheelRF.setTargetPosition(distance);


        if (direction.equals("FORWARD")) {
            wheelLF.setPower(-1);
            wheelRF.setPower(-1);
            wheelLB.setPower(-1);
            wheelRB.setPower(-1);


            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() > -distance && wheelRF.getCurrentPosition() > -distance) {
                telemetry.addData("0", String.format("Moving forward, please stand by..."));
                telemetry.update();
            }

        }
        else if (direction.equals("BACKWARD")) {
            wheelLF.setPower(1);
            wheelRF.setPower(1);
            wheelLB.setPower(1);
            wheelRB.setPower(1);


            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() < distance && wheelRF.getCurrentPosition() < distance) {
                telemetry.addData("0", String.format("Moving backward, please stand by..."));
                telemetry.update();
            }


        }
        else if (direction.equals("CRAB RIGHT")) { //
            //todo: check crab right method
            wheelRF.setPower(1);
            wheelRB.setPower(-1);
            wheelLF.setPower(-1);
            wheelLB.setPower(1);

            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() > distance && wheelRF.getCurrentPosition() < distance) {
                telemetry.addData("0", String.format("Crabbing right, please stand by..."));
                telemetry.update();
            }
        }
        else if (direction.equals("CRAB LEFT")) {
            //todo: check crab left method
            wheelRF.setPower(-1);
            wheelRB.setPower(1);
            wheelLF.setPower(1);
            wheelLB.setPower(-1);

            // check to see if robot is done yet
            while (wheelLF.getCurrentPosition() < distance && wheelRF.getCurrentPosition() > distance) {
                telemetry.addData("0", String.format("Crabbing right, please stand by..."));
                telemetry.update();
            }
        }
        else {
            wheelLF.setPower(0);
            wheelRF.setPower(0);
            wheelLB.setPower(0);
            wheelRB.setPower(0);
            telemetry.addData("0", String.format("YOU DIDN'T PASS IN A VALID DIRECTION. I'M NOT MOVING."));
            telemetry.update();
        }

        }


    public void driveGyro(int degrees) {
        //todo: create driveGyro method that takes in degrees to turn
    }


}
