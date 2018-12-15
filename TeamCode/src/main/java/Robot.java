package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.util.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;


public class Robot extends java.lang.Thread {

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private static final int TICKS_PER_ROTATION = 1440; //Tetrix motor specific
    private static final int WHEEL_DIAMETER = 6; //Wheel diameter in inches
    public  String TAG = "FTC_APP";

    public Gyroscope imu;
    public CRServo grabber_1;
    public CRServo holder;
    public DcMotor motor_0;
    public DcMotor motor_1;
    public DcMotor motor_2;
    public DcMotor motor_3;
    public DcMotor motor_4;
    public DcMotor motor_5;
    public DcMotor motor_6;
    public DcMotor latch;
    public CRServo teammarker;
    public ElapsedTime mRuntime;


    public boolean debugOn = false;
    public boolean isTeleOp = true;
    public int     inverse = 1;
    public boolean DEBUG = true;

    public long movementFactor = 1;
    public long slideFactor = 1;
    public long grabberFactor = 1;
    public long turnFactor = 1;
    private boolean detect = true; // For debugging purpose

    Robot (HardwareMap map, Telemetry tel) {
        hardwareMap = map;
        telemetry = tel;
        initDevices ();
    }

    public void pause () {
        try {
            sleep(250);
        } catch (Exception e) {}
    }


    // This function takes input distance in inches and will return Motor ticks needed
    // to travel that distance based on wheel diameter
    public int DistanceToTick(int distance) {

        double circumference = WHEEL_DIAMETER * 3.14;

        double num_rotation  = distance/circumference;

        int encoder_ticks    = (int)(num_rotation * TICKS_PER_ROTATION);
        telemetry.addData("DistancetoTick Rotation", num_rotation);
        telemetry.addData("DistancetoTick ticks", encoder_ticks);
        telemetry.update();

        return (encoder_ticks);
    }

    // This function takes input Angle (in degrees)  and it will return Motor ticks needed
    // to make that Turn2
    public int AngleToTick(double angle) {

      //  double num_rotation  = (double) (java.lang.Math.abs(angle)/360);
        int encoder_ticks    = (int)((java.lang.Math.abs(angle) * TICKS_PER_ROTATION)/360);
      //  telemetry.addData("AngletoTick Rotation", num_rotation);
        telemetry.addData("AngletoTick ticks", encoder_ticks);
        telemetry.update();
        try {
            sleep(2000);
        } catch (Exception e) {}

        return (encoder_ticks);
    }

    // Come down using encoder moveToPosition and come out of latch
    public void unlatchUsingEncoderPosition (double power){
        telemetry.addData("Direction", "Down");
        telemetry.update();
        Log.i(TAG, "Enter Function: unlatchUsingEncoderPosition");
        Log.i(TAG, "Starting decent at : " + System.currentTimeMillis());

        // Reset encoder
        motor_5.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Trial and Error
        int ticks = 10 * TICKS_PER_ROTATION;
        telemetry.addData("Actual Ticks needed", ticks);
        telemetry.update();

        // Set the target position and power and run to position
        motor_5.setTargetPosition(ticks);
        motor_5.setPower(power);
        motor_5.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        //Wait for them to reach to the position
        while (motor_5.isBusy()){
            //Waiting for Robot to travel the distance
            telemetry.addData("Down", "Moving");
            telemetry.addData("Current Motor 5", motor_5.getCurrentPosition());
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        motor_5.setPower(0);
        Log.i(TAG, "Completed decent at : " + System.currentTimeMillis());

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 5", motor_5.getCurrentPosition());
            telemetry.update();

            Log.i(TAG, "Moving Down");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 5" + motor_5.getCurrentPosition() );
        }

        telemetry.addData("Down", "Completed");
        telemetry.update();
        Log.i(TAG, "Exit Function: unlatchUsingEncoderPosition");
    }

    // Come down using encoder speed and come out of latch
    public void unlatchUsingEncoderSpeed (double power){
        telemetry.addData("Direction", "Down");
        telemetry.update();
        Log.i(TAG, "Enter Function: unlatchUsingEncoderSpeed");
        Log.i(TAG, "Starting decent at : " + System.currentTimeMillis());

        motor_5.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_5.setPower(power);

        try {
            sleep(4000);
        } catch (Exception e) {}

        //Reached the distance, so stop the motors
        motor_5.setPower(0);

        Log.i(TAG, "Completed decent at : " + System.currentTimeMillis());
        telemetry.addData("Down", "Completed");
        telemetry.update();
        Log.i(TAG, "Exit Function: unlatchUsingEncoderSpeed");
    }


    // Move forward to specific distance in inches, with power (0 to 1)
    public void moveForward (double power, int distance){
        telemetry.addData("Direction", "Forward");
        telemetry.update();

        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);
        telemetry.addData("Actual Ticks needed", ticks);
        telemetry.update();

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition(ticks);
        motor_1.setTargetPosition((-1) * ticks);
        motor_2.setTargetPosition(ticks);
        motor_3.setTargetPosition((-1) * ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (motor_0.isBusy() || motor_1.isBusy() || motor_2.isBusy() || motor_3.isBusy() ){
            //Waiting for Robot to travel the distance
            telemetry.addData("Forward", "Moving");
            telemetry.addData("Current Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Current Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Current Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Current Motor 3", motor_3.getCurrentPosition());
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 3", motor_3.getCurrentPosition());
            telemetry.update();

            Log.i(TAG, "Moving Forward");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 0" + motor_0.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 1" + motor_1.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 2" + motor_2.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 3" + motor_3.getCurrentPosition() );
        }

        telemetry.addData("Forward", "Completed");
        telemetry.update();
    }

    // Move backward to specific distance in inches, with power (0 to 1)
    public void moveBackward (double power, int distance){
        telemetry.addData("Direction", "Backward");
        telemetry.update();

        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition((-1) * ticks);
        motor_1.setTargetPosition(ticks);
        motor_2.setTargetPosition((-1) * ticks);
        motor_3.setTargetPosition(ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (motor_0.isBusy() || motor_1.isBusy() || motor_2.isBusy() || motor_3.isBusy() ){
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 3", motor_3.getCurrentPosition());
            telemetry.update();
            Log.i(TAG, "Moving Backward");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 0" + motor_0.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 1" + motor_1.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 2" + motor_2.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 3" + motor_3.getCurrentPosition() );
        }

        telemetry.addData("Backward", "Completed");
        telemetry.update();
    }

    // Move Left to specific distance in inches, with power (0 to 1)
    public void moveLeft (double power, int distance){
        telemetry.addData("Direction", "Left");
        telemetry.update();

        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition(ticks);
        motor_1.setTargetPosition( ticks);
        motor_2.setTargetPosition((-1) * ticks);
        motor_3.setTargetPosition((-1) * ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (motor_0.isBusy() || motor_1.isBusy() || motor_2.isBusy() || motor_3.isBusy() ){
            //Waiting for Robot to travel the distance
            telemetry.addData("Left", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 3", motor_3.getCurrentPosition());
            telemetry.update();
            Log.i(TAG, "Moving Left");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 0" + motor_0.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 1" + motor_1.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 2" + motor_2.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 3" + motor_3.getCurrentPosition() );
        }

        telemetry.addData("Left", "Completed");
        telemetry.update();
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void moveRight (double power, int distance){
        telemetry.addData("Direction", "Right");
        telemetry.update();

        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition((-1) * ticks);
        motor_1.setTargetPosition((-1) * ticks);
        motor_2.setTargetPosition(ticks);
        motor_3.setTargetPosition(ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (motor_0.isBusy() || motor_1.isBusy() || motor_2.isBusy() || motor_3.isBusy() ){
            //Waiting for Robot to travel the distance
            telemetry.addData("Right", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 3", motor_3.getCurrentPosition());
            telemetry.update();
            Log.i(TAG, "Moving Right");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 0" + motor_0.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 1" + motor_1.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 2" + motor_2.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 3" + motor_3.getCurrentPosition() );
        }

        telemetry.addData("Right", "Completed");
        telemetry.update();
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void turnNew (double power, int angle){
        telemetry.addData("Turn", "In function");
        telemetry.update();

        try {
            sleep(1000);
        } catch (Exception e) {}

        int orientation = 1;
        if (angle > 0) {
            telemetry.addData("Turn", "Clockwise");
            telemetry.update();
        } else {
            orientation = -1;
            telemetry.addData("Turn", "Clockwise");
            telemetry.update();
        }
        try {
            sleep(1000);
        } catch (Exception e) {}
        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = AngleToTick(angle);

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition(orientation * ticks);
        motor_1.setTargetPosition(orientation * ticks);
        motor_2.setTargetPosition(orientation * ticks);
        motor_3.setTargetPosition(orientation * ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (motor_0.isBusy() || motor_1.isBusy() || motor_2.isBusy() || motor_3.isBusy() ){
            //Waiting for Robot to travel the distance
            telemetry.addData("Right", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG){
            telemetry.addData("Actual Ticks needed", ticks);
            telemetry.addData("Actual Ticks Motor 0", motor_0.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 1", motor_1.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 2", motor_2.getCurrentPosition());
            telemetry.addData("Actual Ticks Motor 3", motor_3.getCurrentPosition());
            telemetry.update();
            Log.i(TAG, "Moving Right");
            Log.i(TAG, "TICKS needed" + ticks );
            Log.i(TAG, "Actual Ticks Motor 0" + motor_0.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 1" + motor_1.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 2" + motor_2.getCurrentPosition() );
            Log.i(TAG, "Actual Ticks Motor 3" + motor_3.getCurrentPosition() );
        }

        telemetry.addData("Right", "Completed");
        telemetry.update();
    }

    public void moveB (long distance) {
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        motor_0.setPower(-0.5);
        motor_1.setPower(0.5);
        motor_2.setPower(-0.5);
        motor_3.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        pause();
    }

    public void moveF (long distance) {

        motor_0.setPower(0.5);
        motor_1.setPower(-0.5);
        motor_2.setPower(0.5);
        motor_3.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        telemetry.addData("Direction", "Forward");
        telemetry.update();
        try {
            sleep(2000);
        } catch (Exception e) {}
        pause();
    }

    public void moveR (long distance) {
        motor_0.setPower(0.5);
        motor_1.setPower(0.5);
        motor_2.setPower(-0.5);
        motor_3.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Left");
        telemetry.update();
        pause();
    }

    public void moveL (long distance) {
        motor_0.setPower(-0.5);
        motor_1.setPower(-0.5);
        motor_2.setPower(0.5);
        motor_3.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Right");
        telemetry.update();
        pause();
    }
    public void moveFL (long distance) {
        motor_0.setPower(0.5);
        motor_3.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveFR (long distance) {
        motor_1.setPower(-0.5);
        motor_2.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveBL (long distance) {
        motor_1.setPower(0.5);
        motor_2.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveBR (long distance) {
        motor_0.setPower(-0.5);
        motor_3.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void turn (long angle) {
        if (angle > 0) {
            motor_0.setPower(1);
            motor_1.setPower(1);
            motor_2.setPower(1);
            motor_3.setPower(1);
            try {
                sleep(angle * turnFactor);
            } catch (Exception e) {}
            motor_0.setPower(0);
            motor_1.setPower(0);
            motor_2.setPower(0);
            motor_3.setPower(0);
            pause();
            pause();
        } else {
            motor_0.setPower(-1);
            motor_1.setPower(-1);
            motor_2.setPower(-1);
            motor_3.setPower(-1);
            try {
                sleep(angle * -turnFactor);
            } catch (Exception e) {}
            motor_0.setPower(0);
            motor_1.setPower(0);
            motor_2.setPower(0);
            motor_3.setPower(0);
            pause();
            pause();
        }
    }
    public void slow_turn (long angle) {
        if (angle > 0) {
            motor_0.setPower(0.5);
            motor_1.setPower(0.5);
            motor_2.setPower(0.5);
            motor_3.setPower(0.5);
            try {
                sleep(angle * turnFactor);
            } catch (Exception e) {}
            motor_0.setPower(0);
            motor_1.setPower(0);
            motor_2.setPower(0);
            motor_3.setPower(0);
            if (isTeleOp == false) pause(250);
            if (isTeleOp == false) pause(250);
        } else {
            motor_0.setPower(-0.5);
            motor_1.setPower(-0.5);
            motor_2.setPower(-0.5);
            motor_3.setPower(-0.5);
            try {
                sleep(-1 * angle * turnFactor);
            } catch (Exception e) {}
            motor_0.setPower(0);
            motor_1.setPower(0);
            motor_2.setPower(0);
            motor_3.setPower(0);
            if (isTeleOp == false) pause(250);
            if (isTeleOp == false) pause(250);
        }
    }

    public void wall_align(double power) {
        motor_0.setPower(power);
        motor_1.setPower(power);
        motor_2.setPower(-1 * power);
        motor_3.setPower(-1 * power);
        try {
            sleep(2100);
        } catch (Exception e) {}
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
    }

    //The following two methods were removed due to the fact that we removed the grabber.
    /*public void moveG (long distance) {
        if (distance > 0) {
            motor_4.setPower(-1);
            try {
                sleep(distance * grabberFactor);
            } catch (Exception e) {}
            motor_4.setPower(0);
        } else {
            motor_4.setPower(1);
            try {
                sleep(-1 * distance * grabberFactor);
            } catch (Exception e) {}
            motor_4.setPower(0);
        }
    }

    public void grab() {
        grabber_1.setPower(1);
        try {
            sleep(2000);
        } catch (Exception e) {}
        grabber_1.setPower(0);
    } */

    public void release() {
        grabber_1.setPower(-1);
        try {
            sleep(2000);
        } catch (Exception e) {}
        grabber_1.setPower(0);
    }

    public void slide (long distance) {
        if (distance >= 0) {
            motor_5.setPower(1);
            try {
                sleep(distance * slideFactor);
            } catch (Exception e) {}
            motor_5.setPower(0);
        } else {
            motor_5.setPower(-1);
            try {
                sleep(-1 * distance * slideFactor);
            } catch (Exception e) {}
            motor_5.setPower(0);
        }
    }

    public void unlatch() {
        latch.setPower(-1);
        try {
            sleep(100);
        } catch (Exception e){}
        latch.setPower(0);
    }

    public void drop_marker() {
        teammarker.setPower(1);
        try {
            sleep(400);
            teammarker.setPower(0);
        } catch (Exception e){}
    }
    public void hold_slide() {
        holder.setPower(1);
        try {
            sleep(50);
        } catch (Exception e){}
    }

    public void relatch() {
        latch.setPower(-1);
        try {
            sleep(100);
        } catch (Exception e){}

    }
    public void pause(long sleep){
        try {
            sleep(sleep);
        } catch (Exception e){}

    }

    private void initDeviceCore() throws Exception {

        telemetry.addData("Please wait","In function init devices");
        telemetry.update();

        imu = hardwareMap.get(Gyroscope.class, "imu");

        //Servo for rotating the grabber
        teammarker = hardwareMap.get(CRServo.class, "teammarker");
        grabber_1 = hardwareMap.get(CRServo.class, "grabber_1");
        holder = hardwareMap.get(CRServo.class, "holder");
        //Wheels
        motor_0 = hardwareMap.get(DcMotor.class, "motor_br");
        motor_1 = hardwareMap.get(DcMotor.class, "motor_bl");
        motor_2 = hardwareMap.get(DcMotor.class, "motor_fr");
        motor_3 = hardwareMap.get(DcMotor.class, "motor_fl");


        //Grabber Motor
        motor_4 = hardwareMap.get(DcMotor.class, "grabber_lift");

        //Grabber Slide
        motor_6 = hardwareMap.get(DcMotor.class, "grabber_slide");

        //Hanger
        motor_5 = hardwareMap.get(DcMotor.class, "hanger");

        //Latch
        latch = hardwareMap.get(DcMotor.class, "latch");

        telemetry.addData("Status", "Initialized");
        telemetry.update();


    }

    private void initDevices() {
        mRuntime = new ElapsedTime();
        mRuntime.reset();

        try {
            initDeviceCore();
        } catch (Exception e) {
            telemetry.addData("Exception", "In function init devices" + e);
            telemetry.update ();
            try {sleep(10000);} catch (Exception e1) {}

        }

    }


}
