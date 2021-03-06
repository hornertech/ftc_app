package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;


public class Robot extends java.lang.Thread {

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private static final int TICKS_PER_ROTATION = 1440; //Tetrix motor specific
    private static final int WHEEL_DIAMETER = 6; //Wheel diameter in inches
    public String TAG = "FTC_APP";

    public DcMotor motor_0;
    public DcMotor motor_1;
    public DcMotor motor_2;
    public DcMotor motor_3;

    public DcMotor latch;
    public DcMotor grabber_rotater;
    public DcMotor grabber_slide;
    public DcMotor grabber_1;

    public ElapsedTime mRuntime;

    public boolean isTeleOp = true;
    public boolean DEBUG_DEBUG = false;
    public boolean DEBUG_INFO = false;

    public long movementFactor = 1;
    public long turnFactor = 1;

    Robot(HardwareMap map, Telemetry tel) {
        hardwareMap = map;
        telemetry = tel;
        initDevices();
    }

    public void pause() {
        try {
            sleep(250);
        } catch (Exception e) {
        }
    }

    // This function takes input distance in inches and will return Motor ticks needed
    // to travel that distance based on wheel diameter
    public int DistanceToTick(int distance) {
       // Log.i(TAG, "Enter FUNC: DistanceToTick");

        double circumference = WHEEL_DIAMETER * 3.14;
        double num_rotation = distance / circumference;
        int encoder_ticks = (int) (num_rotation * TICKS_PER_ROTATION);

        //       Log.i(TAG,"Rotation Needed : " + num_rotation);
        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Needed : " + encoder_ticks);
            Log.i(TAG, "Exit FUNC: DistanceToTick");
        }

        return (encoder_ticks);
    }
    // This function takes input Angle (in degrees)  and it will return Motor ticks needed
    // to make that Turn2
    public int AngleToTick(double angle) {
        Log.i(TAG, "Enter FUNC: AngleToTick");

        int encoder_ticks = (int) ((java.lang.Math.abs(angle) * TICKS_PER_ROTATION ) / 360);

        Log.i(TAG, "Ticks needed for Angle : " + encoder_ticks);
        Log.i(TAG, "Exit FUNC: AngleToTick");

        return (encoder_ticks);
    }

     // Come down using encoder moveToPosition and come out of latch
    public void unlatchUsingEncoderPosition(double power, int direction, double rotattion) {
        Log.i(TAG, "Enter Function: unlatchUsingEncoderPosition");
        long time = System.currentTimeMillis();

        // Reset encoder
        latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Trial and Error
        int ticks = (int) (rotattion * TICKS_PER_ROTATION);

        // Set the target position and power and run to position
        latch.setTargetPosition((direction) * ticks);
        latch.setPower(power);
        latch.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        //Wait for them to reach to the position
        while (latch.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Down", "Moving");
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        latch.setPower(0);

        long time_taken = System.currentTimeMillis() - time;
        Log.i(TAG, "Time taken for decent : " + time_taken);
        Log.i(TAG, "Exit Function: unlatchUsingEncoderPosition");
    }

    /*****************************************************************************/
    /* Section:      Move to specific distance functions                         */
    /*                                                                           */
    /* Purpose:    Used for moving motor specific inches                         */
    /*                                                                           */
    /* Returns:   Nothing                                                        */
    /*                                                                           */
    /* Params:    IN     power         - Speed  (-1 to 1)                        */
    /*            IN     distance      -  in inches                              */
    /*                                                                           */
    /*****************************************************************************/
     // Move forward to specific distance in inches, with power (0 to 1)
    public void moveForwardToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveForwardToPosition Power : " + power + " and distance : " + distance);
        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

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
        //  while ((motor_2.isBusy() && motor_3.isBusy()) || (motor_1.isBusy() && motor_0.isBusy())){
        while (motor_0.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveForwardToPosition");
        }
    }

    // Move backward to specific distance in inches, with power (0 to 1)
    public void moveBackwardToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveBackwardToPosition Power : " + power + " and distance : " + distance);

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
        motor_0.setPower(power * 0.9);
        motor_1.setPower(power);
        motor_2.setPower(power);
        motor_3.setPower(power * 1.1);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        // while ((motor_0.isBusy() && motor_3.isBusy()) || (motor_1.isBusy() && motor_2.isBusy())){
        while (motor_0.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveBackwardToPosition");
        }
    }

    // Move Left to specific distance in inches, with power (0 to 1)
    public void moveLeftToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveLeftToPosition Power : " + power + " and distance : " + distance);

        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        motor_0.setTargetPosition(ticks);
        motor_1.setTargetPosition(ticks);
        motor_2.setTargetPosition((-1) * ticks);
        motor_3.setTargetPosition((-1) * ticks);

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower(power * 1.03);
        motor_2.setPower(power * 1.06);
        motor_3.setPower(power * 1.08);

        //Set Motors to RUN_TO_POSITION
        motor_0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor_3.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        //Wait for them to reach to the position
        // while ((motor_1.isBusy() && motor_3.isBusy()) || (motor_0.isBusy() && motor_2.isBusy())){
        while (motor_0.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveLeftToPosition");
        }
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void moveRightToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveRightToPosition Power : " + power + " and distance : " + distance);

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
        // while ((motor_0.isBusy() && motor_2.isBusy()) || (motor_1.isBusy() && motor_3.isBusy())){
        while (motor_0.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveRightToPosition");
        }
    }
    // Move Right to specific distance in inches, with power (0 to 1)
    public void turnWithAngleAnticlockwise(double power, int angle) {
        Log.i(TAG, "Enter Function: moveRight Power : " + power + " and angle : " + angle);

        int orientation = 1;
        if (angle > 0) {
            Log.i(TAG, "Turning Clockwise");
        } else {
            orientation = -1;
            Log.i(TAG, "Turning Anti-Clockwise");
        }
        try {
            sleep(1000);
        } catch (Exception e) {
        }
        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = AngleToTick(angle);
        ticks = (int) (ticks * 2.85);
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
        while (motor_0.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Turning", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        Log.i(TAG, "TICKS needed : " + ticks);
        Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
        Log.i(TAG, "Exit Function: turnNew");
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void turnWithAngleClockwise (double power, int angle) {
        Log.i(TAG, "Enter Function: moveRight Power : " + power + " and angle : " + angle);

        int orientation = 1;
        if (angle > 0) {
            Log.i(TAG, "Turning Clockwise");
        } else {
            orientation = -1;
            Log.i(TAG, "Turning Anti-Clockwise");
        }
        try {
            sleep(1000);
        } catch (Exception e) {
        }
        // Reset all encoders
        motor_0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor_3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = AngleToTick(angle);
        ticks = (int) (ticks * 3.5)* (-1);
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
        while (motor_0.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Turning", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        Log.i(TAG, "TICKS needed : " + ticks);
        Log.i(TAG, "Actual Ticks Motor0 : " + motor_0.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor1 : " + motor_1.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor2 : " + motor_2.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor3 : " + motor_3.getCurrentPosition());
        Log.i(TAG, "Exit Function: turnNew");
    }
    /*****************************************************************************/
    /* Section:      Move For specific time functions                            */
    /*                                                                           */
    /* Purpose:    Used if constant speed is needed                              */
    /*                                                                           */
    /* Returns:   Nothing                                                        */
    /*                                                                           */
    /* Params:    IN     power         - Speed  (-1 to 1)                        */
    /*            IN     time          - Time in MilliSeconds                    */
    /*                                                                           */
    /*****************************************************************************/
    // Move forward for specific time in milliseconds, with power (0 to 1)
    public void moveForwardForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveForwardForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower((-1) * power);
        motor_2.setPower(power);
        motor_3.setPower((-1) * power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveForwardForTime");
        }
    }

    public void moveBackwardForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveBackwardForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        motor_0.setPower((-1) * power);
        motor_1.setPower(power);
        motor_2.setPower((-1) * power);
        motor_3.setPower(power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveBackwardForTime");
        }
    }

    public void moveRightForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveRightForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            motor_0.setPower((-1) * power);
            motor_1.setPower((-1) * power);
            motor_2.setPower(power);
            motor_3.setPower(power);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            motor_0.setPower((-1) * power);
            motor_1.setPower((-1) * power);
            motor_2.setPower(power);
            motor_3.setPower(power);
        }

        try {
            sleep(time);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveRightForTime");
        }
    }

    public void moveLeftForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveLeftForTime Power : " + power + " and time : " + time + "Speed : " + speed);

        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            motor_0.setPower(power);
            motor_1.setPower(power);
            motor_2.setPower((-1) * power);
            motor_3.setPower((-1) * power);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            motor_0.setPower(power);
            motor_1.setPower(power );
            motor_2.setPower((-1) * power);
            motor_3.setPower((-1) * power);
        }

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO ) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));

            Log.i(TAG, "Exit Function: moveLeftForTime");
        }
    }

    public void moveForwardAndDropSlide(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveForwardAndDropSlide Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        motor_0.setPower(power);
        motor_1.setPower((-1) * power);
        motor_2.setPower(power);
        motor_3.setPower((-1) * power);
        grabber_rotater.setPower(1);
        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        grabber_rotater.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveForwardForTime");
        }
    }

    public void moveRightAndDropSlide(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveRightForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            motor_0.setPower((-1) * power);
            motor_1.setPower((-1) * power);
            motor_2.setPower(power);
            motor_3.setPower(power);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            motor_0.setPower((-1) * power);
            motor_1.setPower((-1) * power);
            motor_2.setPower(power);
            motor_3.setPower(power);
        }
        grabber_rotater.setPower(1);

        try {
            sleep(time);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        grabber_rotater.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveRightForTime");
        }
    }
    public void moveBackwardAndExtendSlide(double power,double slide_power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveBackwardForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = motor_0.getCurrentPosition();
        long motor1_start_position = motor_1.getCurrentPosition();
        long motor2_start_position = motor_2.getCurrentPosition();
        long motor3_start_position = motor_3.getCurrentPosition();

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        motor_0.setPower((-1) * power);
        motor_1.setPower(power);
        motor_2.setPower((-1) * power);
        motor_3.setPower(power);
        grabber_slide.setPower(slide_power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        grabber_slide.setPower(0);

        long motor0_end_position = motor_0.getCurrentPosition();
        long motor1_end_position = motor_1.getCurrentPosition();
        long motor2_end_position = motor_2.getCurrentPosition();
        long motor3_end_position = motor_3.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));
            Log.i(TAG, "Exit Function: moveBackwardForTime");
        }
    }

    public void turnForTime(double power, int time, boolean speed, int orientation) {
        Log.i(TAG, "Enter Function: turnForTime Power : " + power + " and time : " + time + "Speed : " + speed + "orientation : " + orientation);

        if (speed == true) {
            motor_0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            motor_0.setPower(orientation * power);
            motor_1.setPower(orientation * power);
            motor_2.setPower(orientation * power);
            motor_3.setPower(orientation * power);
        } else {
            motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            //motor_0.setPower(orientation * power * 1.6);
            motor_0.setPower(orientation * power);
            motor_1.setPower(orientation * power);
            motor_2.setPower(orientation * power);
           //motor_3.setPower(orientation * power * 1.5);
            motor_3.setPower(orientation * power);
        }

      /*  motor_0.setPower(orientation * power * 0.75);
        motor_1.setPower(orientation * power * 1.5);
        motor_2.setPower(orientation * power * 1.5);
        motor_3.setPower(orientation * power *0.75); */
        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
    }



    public void moveB(long distance) {
        telemetry.addData("Direction", "Forward");
        telemetry.update();
        motor_0.setPower(-0.8);
        motor_1.setPower(0.8);
        motor_2.setPower(-0.8);
        motor_3.setPower(0.8);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Forward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveF(long distance) {
        motor_0.setPower(0.8);
        motor_1.setPower(-0.8);
        motor_2.setPower(0.8);
        motor_3.setPower(-0.8);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveR(long distance) {
        double power = 0.8;

        motor_0.setPower(power );
        motor_1.setPower(power);
        motor_2.setPower((-1) * power);
        motor_3.setPower((-1) * power);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Left");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveL(long distance) {
        double power = 0.8;

        motor_0.setPower((-1) * power);
        motor_1.setPower((-1) * power);
        motor_2.setPower(power );
        motor_3.setPower(power);

        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Right");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveFL(long distance) {
        motor_0.setPower(0.5);
        motor_3.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveFR(long distance) {
        motor_1.setPower(-0.5);
        motor_2.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveBL(long distance) {
        motor_1.setPower(0.5);
        motor_2.setPower(-0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveBR(long distance) {
        motor_0.setPower(-0.5);
        motor_3.setPower(0.5);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void slow_turn(long angle) {
        motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (angle > 0) {
            motor_0.setPower(0.5);
            motor_1.setPower(0.5);
            motor_2.setPower(0.5);
            motor_3.setPower(0.5);
            try {
                sleep(angle * turnFactor);
            } catch (Exception e) {
            }
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
            } catch (Exception e) {
            }
            motor_0.setPower(0);
            motor_1.setPower(0);
            motor_2.setPower(0);
            motor_3.setPower(0);
            if (isTeleOp == false) pause(250);
            if (isTeleOp == false) pause(250);
        }
    }

    //CLEANUP: remove this and use moveLeft/RightForTime
    public void wall_align(double power, int time) {
        motor_0.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor_0.setPower(-1 * power);
        motor_1.setPower(-1 * power);
        motor_2.setPower(power);
        motor_3.setPower(power);
        try {
            sleep(time);
        } catch (Exception e) {
        }
        motor_0.setPower(0);
        motor_1.setPower(0);
        motor_2.setPower(0);
        motor_3.setPower(0);
    }

    public void grabberRotatorMovePosition(double power, int position) {
        grabber_rotater.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabber_rotater.setPower(power);
        grabber_rotater.setTargetPosition(position);
        grabber_rotater.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (grabber_rotater.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Grabber", "Extending");
        }

        grabber_rotater.setPower(0);
    }

    public void grabberRotatorMoveTime(double power, int time) {
        grabber_rotater.setPower(power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        grabber_rotater.setPower(0);
    }

    public void grabberSlideMovePosition(double power, int position) {
        grabber_slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabber_slide.setPower(power);
        grabber_slide.setTargetPosition(position);
        grabber_slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (grabber_slide.isBusy()) {
            telemetry.addData("Grabber", "Extending");
            telemetry.update();
        }

        grabber_slide.setPower(0);
    }

    public void grabberSlideMoveTime(double power, int time) {
        grabber_slide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        grabber_slide.setPower(power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        grabber_slide.setPower(0);
    }


    public void grabberExtendSlide(double   rotator_power,
                                   double   slide_power,
                                   int      rotator_position,
                                   int      slide_position) {
        grabberRotatorMovePosition(rotator_power, rotator_position);
        grabberSlideMovePosition(slide_power, slide_position);
    }

    public void grabMineral(int time) {
        grabber_1.setPower(-0.25);
        try {
            sleep(time*100);
        } catch (Exception e) {
        }

        grabber_1.setPower(0);
    }

    public void releaseMineral(int time) {
        grabber_1.setPower(1);
        try {
            sleep(time * 100);
        } catch (Exception e) {
        }
        grabber_1.setPower(0);
    }

    public void pause(long sleep) {
        try {
            sleep(sleep);
        } catch (Exception e) {
        }

    }

    private void initDeviceCore() throws Exception {

        telemetry.addData("Please wait", "In function init devices");
        telemetry.update();

        //Wheels
        motor_0 = hardwareMap.get(DcMotor.class, "motor_br");
        motor_1 = hardwareMap.get(DcMotor.class, "motor_bl");
        motor_2 = hardwareMap.get(DcMotor.class, "motor_fr");
        motor_3 = hardwareMap.get(DcMotor.class, "motor_fl");


        //Latch
        latch = hardwareMap.get(DcMotor.class, "latch");

        grabber_rotater = hardwareMap.get(DcMotor.class, "grabber_rotater");
        grabber_slide = hardwareMap.get(DcMotor.class, "grabber_slide");

        grabber_1 = hardwareMap.get(DcMotor.class, "grabber_1");

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
            telemetry.update();
            try {
                sleep(10000);
            } catch (Exception e1) {
            }

        }

    }


}
