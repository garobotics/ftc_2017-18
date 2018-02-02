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
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by ga on 1/8/2018.
 */

@Autonomous(name="Red Autonomous")

public class redAutonomous extends LinearOpMode {

    public DcMotor wheelRF;
    public DcMotor wheelRB;
    public DcMotor wheelLF;
    public DcMotor wheelLB;
    public Servo jewelArm;
    public ColorSensor colorSensor;
    public DcMotor glyphlyft;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F,0F,0F};
    String ballColor;
    final int liftHeight = 1000;


    HardwarePushbot robot   = new HardwarePushbot();   // Use a Pushbot's hardware
    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    // ~just vuforia things~
    public static final String TAG = "Vuforia Relic Recovery";

    OpenGLMatrix lastLocation = null;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;


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
        glyphlyft = hardwareMap.dcMotor.get("glyphlyft");



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
        glyphlyft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // set drive power to 0
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);

        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        /*
         * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
         * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
         * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
         * web site at https://developer.vuforia.com/license-manager.
         *
         * Vuforia license keys are always 380 characters long, and look as if they contain mostly
         * random data. As an example, here is a example of a fragment of a valid key:
         *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         * Once you've obtained a license key, copy the string from the Vuforia web site
         * and paste it in to your code onthe next line, between the double quotes.
         */
        parameters.vuforiaLicenseKey = "AY7wJZH/////AAAAGZMdB1/VKEVukhg26LQV42lceWeAr1LKIKyASsN63SUKG2y0cw4j0jxeOOY3MqgM0teJz8kyQGCaPpFEu0kXsblybBfCo+Ta0PZapJYWFCzk+NdiJIK7iy29OqFh7/vFMrdcl6i1iVX4We5Xvjr2XpYoJFd2m2RrUFrU6+vmv3RYYmLJynLI3IGP1jpHU6XZVPukzimvB1ABs6AelwYwUHzlXX/tloA4PuTLhhwUYRIzX948sQUr6Vr26fnZWPHLY/rJ0HyyTPaIUVro+giCdp8rVQoYBKbu+f7UTuN7r1H/XvyofXR6OlFLHi0SdQy91sRr3ER8I6iY19OwkhBOqQMzcpu6DK7A7Lik0J/EOnS1";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // METHODS GO HERE: THIS ACTS AS THE MAIN METHOD THAT CALLS ALL THE OTHER METHODS
        // run the vuforia method and store the result as a variable

        relicTrackables.activate();
        RelicRecoveryVuMark retImage = detectPattern(relicTemplate); // detect the pictogram pattern
        // telemetry the output of the method
        telemetry.addData("4", retImage);
        telemetry.update();
        //grabGlyph();
        //liftGlyph();

        // todo this is what we actually do
        knockJewel("blue"); // knock the blue jewel off the platform

        driveEncoders(1, 5, "backward", 1); // drive backward off the balance board
        turn(1, 20, "right", 15);


        //driveEncoders(0.25, 2, "backward", 4); // drive forward to make sure we are in the triangle

        //sleep(1000);
        driveToBox(retImage); // drive to the correct location in front of the crypto box
        //placeGlyph();

        end();

    }

    public void end(){
        wheelRF.setPower(0.0);
        wheelRB.setPower(0.0);
        wheelLF.setPower(0.0);
        wheelLB.setPower(0.0);
    }

    public void driveEncoders(double speed, double distance, String direction, double timeoutS) {
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
                telemetry.addData("4", String.format("You didn't pass a valid direction. I refuse to move."));
                telemetry.update();
            }

            // Turn On RUN_TO_POSITION
            wheelLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelLB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addData("4", "I'm driving");
            telemetry.update();



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
                telemetry.addData("Distance wheels should go (inches): ", distance); // distance wheels should go
                telemetry.addData("Right front target: " ,wheelRF.getTargetPosition());
                telemetry.addData("Right back target: " ,wheelRB.getTargetPosition());
                telemetry.addData("Left front target: " ,wheelLF.getTargetPosition());
                telemetry.addData("Left Back target: " ,wheelLB.getTargetPosition());

                // show the current position of all four wheels
                telemetry.addData("Wheel LF",  wheelLF.getCurrentPosition());
                telemetry.addData("Wheel RF", wheelRF.getCurrentPosition());
                telemetry.addData("Wheel RB", wheelRB.getCurrentPosition());
                telemetry.addData("Wheel LB", wheelLB.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion
            wheelLF.setPower(0);
            wheelRF.setPower(0);
            wheelRB.setPower(0);
            wheelLB.setPower(0);
            //sleep(5000);
/*
            // Turn off RUN_TO_POSITION
            wheelLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            wheelRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
*/
            //  sleep(250);   // optional pause after each move
        }
    }

    public void turn(double speed, double distance, String direction, double timeoutS) {
        int newTargetLB; // in ticks
        int newTargetLF;
        int newTargetRF;
        int newTargetRB;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            // Convert inches to ticks
            int inchesToTicks = (int) (distance * COUNTS_PER_INCH);

            if (direction.equalsIgnoreCase("right")) { // if direction is forward
                // change target positions to go forward
                newTargetLB = wheelLB.getCurrentPosition() - inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() - inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() + inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() + inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            } else if (direction.equalsIgnoreCase("left")) { // if direction is backward
                // change target positions to go backward
                newTargetLB = wheelLB.getCurrentPosition() + inchesToTicks;
                newTargetLF = wheelLF.getCurrentPosition() + inchesToTicks;
                newTargetRF = wheelRF.getCurrentPosition() - inchesToTicks;
                newTargetRB = wheelRB.getCurrentPosition() - inchesToTicks;

                // set all wheels to new target positions
                wheelLB.setTargetPosition(newTargetLB);
                wheelLF.setTargetPosition(newTargetLF);
                wheelRF.setTargetPosition(newTargetRF);
                wheelRB.setTargetPosition(newTargetRB);
            } else {
                stop();
                telemetry.addData("4", String.format("You didn't pass a valid direction. I refuse to move."));
                telemetry.update();
            }

            // Turn On RUN_TO_POSITION
            wheelLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelLB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wheelRB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addData("4", "I'm driving");
            telemetry.update();


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
                telemetry.addData("Distance wheels should go (inches): ", distance); // distance wheels should go
                telemetry.addData("Right front target: ", wheelRF.getTargetPosition());
                telemetry.addData("Right back target: ", wheelRB.getTargetPosition());
                telemetry.addData("Left front target: ", wheelLF.getTargetPosition());
                telemetry.addData("Left Back target: ", wheelLB.getTargetPosition());

                // show the current position of all four wheels
                telemetry.addData("Wheel LF", wheelLF.getCurrentPosition());
                telemetry.addData("Wheel RF", wheelRF.getCurrentPosition());
                telemetry.addData("Wheel RB", wheelRB.getCurrentPosition());
                telemetry.addData("Wheel LB", wheelLB.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion
            wheelLF.setPower(0);
            wheelRF.setPower(0);
            wheelRB.setPower(0);
            wheelLB.setPower(0);
            //sleep(5000);
        }
    }

    /* @param direction indicates whether we want the jewel arm to go in or out
    * This method allows us to control the jewelArm to knock off the ball */
    public void setJewelArm(String direction) {
        telemetry.addData("Jewel arm position: ", jewelArm.getPosition());
        telemetry.update();
        sleep(1000);
        if (direction.equals("out")) { // when @param is "out"
            jewelArm.setPosition(0); // put jewel arm out
            telemetry.addData("4", String.format("Jewel arm is out"));

        }
        else if (direction.equals("in")) { // when @param is "in"
            jewelArm.setPosition(0.5); // put jewel arm in
            telemetry.addData("4", String.format("Jewel arm is in"));
        }
        else { // when @param is another word
            jewelArm.setPosition(0.5); // put jewel arm in by default
            telemetry.addData("4", String.format("You didn't pass in a valid direction"));
        }
        telemetry.update();
        sleep(1000);

    }

    /* @param targetColor is the opposite of the team color
       because we want to knock off the other team's jewel
        color sensor faces the front of the robot (off the left)*/
    public void knockJewel(String targetColor){
        // lower the jewel arm so that the color sensor can see a jewel
        setJewelArm("out");
        sleep(2000); // wait 2 seconds
        String jewelColor = convertHueToString();
        telemetry.addData("jewel color is: ", jewelColor);
        telemetry.update();
        sleep(1000);
        // if the jewel is equal to the targetColor, drive forward to knock it off
        if (targetColor.equals(jewelColor)){
            //sleep(1000); // wait 1 second
            driveEncoders(0.25, 3, "forward", 1); // drive forward
            driveEncoders(0.25, 6, "backward", 1);
            telemetry.addData("4", String.format("I see the opponent's color. Driving forward..."));
        }
        // if it's the opposite color, go backward
        else if (!jewelColor.equals("other")) {
            sleep(1000); // wait 1 second
            driveEncoders(0.25, 3, "backward", 1); // drive backward
            telemetry.addData("4", String.format("I see your team's color. Driving backward..."));
        }
        else { // it's "other"
            setJewelArm("in"); // bring the arm back in and don't move
            telemetry.addData("4", String.format("Who turned out the lights? I can't see red or blue."));
        }
        setJewelArm("in");
        //sleep(5000);
        telemetry.update();
        //sleep(5000);

    }

    public String convertHueToString() {

        Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);
        telemetry.addData("HSV hue value: ",hsvValues[0]);
        telemetry.update();
        sleep(2000);
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

    public RelicRecoveryVuMark detectPattern(VuforiaTrackable relicTemplate) {
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;
        while (vuMark == RelicRecoveryVuMark.UNKNOWN) {
            /**
             * See if any of the instances of {@link relicTemplate} are currently visible.
             * {@link RelicRecoveryVuMark} is an enum which can have the following values:
             * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
             * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
             * */

            vuMark = RelicRecoveryVuMark.from(relicTemplate);

            // keep outputting "not visible" while vumark is unknown
            telemetry.addData("VuMark", "not visible");
            telemetry.update();

        }

        // when the vumark is not unknown anymore, return the object
        telemetry.addData("VuMark", "%s visible", vuMark);
        telemetry.update();


        // return vuMark object to end the method
        return vuMark;

    }

    //todo check the measurements for driving in front of the cryptobox columns, right now, left=3, center=10, right=18
    public void driveToBox(RelicRecoveryVuMark picDir) {
        driveEncoders(1, 10, "right", 4);
        sleep(500);

        // now we are going to act on the information that is stored in the variable vumark
        if (picDir.equals(RelicRecoveryVuMark.LEFT)) { // if LEFT picture is detected
            driveEncoders(1, 16, "LEFT", 4); // crab to left column
            driveEncoders(1, 4, "forward", 4);
        }
        else if(picDir.equals(RelicRecoveryVuMark.CENTER)) { // if CENTER picture is detected
            driveEncoders(15, 13, "LEFT", 4); // crab to center column
            driveEncoders(1, 4, "forward", 4);
        }
        else if(picDir.equals(RelicRecoveryVuMark.RIGHT)) { // if RIGHT picture is detected
            driveEncoders(1, 10, "LEFT", 4); // crab to right column
            driveEncoders(1, 4, "forward", 4);
        }
        else {
            telemetry.addData("Driving status: ", "VuMark was not visible, therefore no driving.");
            telemetry.update();
            stop();

        }
    }

    public void liftGlyph() {
        double timeOutL = 2.5;
        glyphlyft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        glyphlyft.setTargetPosition(liftHeight);
        glyphlyft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        glyphlyft.setPower(0.5);

        while (opModeIsActive() &&
                (runtime.seconds() < timeOutL) &&
                (glyphlyft.isBusy())) {

            // Display it for the driver.
            telemetry.addData("Glyph lyft target: " , glyphlyft.getTargetPosition());

            // show the current position of all four wheels
            telemetry.addData("Glyph lyft current: ", glyphlyft.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion
        glyphlyft.setPower(0);
    }

    public void placeGlyph() {
        // set number of seconds motor should run for
        double timeOutL = 2.5;
        glyphlyft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        glyphlyft.setTargetPosition(-liftHeight);
        glyphlyft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        glyphlyft.setPower(0.5);

        while (opModeIsActive() &&
                (runtime.seconds() < timeOutL) &&
                (glyphlyft.isBusy())) {

            // Display it for the driver.
            telemetry.addData("Glyph lyft target: " , glyphlyft.getTargetPosition());

            // show the current position of all four wheels
            telemetry.addData("Glyph lyft current: ", glyphlyft.getCurrentPosition());
            telemetry.update();
        }

        // Stop all motion
        glyphlyft.setPower(0);

        // open the glyph grabbers to release the glyph
        //glyphGripLeft.setPosition(0.5);
        //glyphGripRight.setPosition(0.7);
    }

    public void grabGlyph(){
        // close the grabbers to grab the glyph
        //glyphGripLeft.setPosition(0.6);
        //glyphGripRight.setPosition(0.6);
    }

    public void driveGyro(int degrees) {
        //todo: create driveGyro method that takes in degrees to turn
        //clockwise is positive and counterclockwise is negative degrees
        //like teleop turning but instead of taking joystick entries, it takes a degree entry
    }
}
