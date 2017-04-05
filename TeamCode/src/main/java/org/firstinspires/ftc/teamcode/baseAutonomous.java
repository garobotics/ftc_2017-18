package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by ga on 4/5/17.
 */

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
        wheelRF.setDirection(DcMotor.Direction.FORWARD);
        wheelRB.setDirection(DcMotor.Direction.REVERSE);
        wheelLF.setDirection(DcMotor.Direction.FORWARD);
        wheelLB.setDirection(DcMotor.Direction.REVERSE);


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

        // put other methods from below that we want to call here
        end();
    }

    public void end(){
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }

    //todo: edit this file or add more functionality



}
