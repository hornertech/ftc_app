package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous
public class test_depot extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final int GOLD_MINERAL_FOUND = 0;
    private static final int SILVER_MINERAL_FOUND = 1;
    private static final int NO_MINERAL_FOUND = 2;


    public boolean debugOn = false;
    public boolean test = false;
    public String TAG = "FTC_APP";


    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    public void ZigZag(org.firstinspires.ftc.teamcode.Robot robot, int iteration,
                       int distance, double power) {
        int sleeptime = 100;
        switch (iteration) {
            case 0: {
                Log.i(TAG, "ZigZag Moving Forward");
                robot.moveForwardToPosition(power, distance); //Move forward 2 inch from center
                //  sleep(sleeptime);
                return;
            }

            case 1: {
                Log.i(TAG, "ZigZag Moving Backward");
                robot.moveBackwardToPosition(power, 2 * distance); //Move back 2 inches from center
                //  sleep(sleeptime);
                return;
            }

            case 2: {
                Log.i(TAG, "ZigZag Moving Left");
                robot.moveForwardToPosition(power, distance); //Come to center
                robot.moveLeftToPosition(power, distance); //Move Left 2 inches from center
                //  sleep(sleeptime);
                return;
            }

            case 3: {
                Log.i(TAG, "ZigZag Moving Right");
                robot.moveRightToPosition(power, 2 * distance); // Move 2 inches right from center
                //  sleep(sleeptime);
                return;
            }

            case 4: {
                Log.i(TAG, "ZigZag Coming back to center");
                robot.moveLeftToPosition(power, distance); //Come back to center, detection failed
                //  sleep(sleeptime);
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public void move_center(org.firstinspires.ftc.teamcode.Robot robot, int iteration,
                            int distance, double power) {
        int sleeptime = 100;
        switch (iteration) {
            case 0: {
                Log.i(TAG, "Already At Center");
                return;
            }

            case 1: {
                Log.i(TAG, "Move Backward to come to center");
                robot.moveBackwardToPosition(power, distance);
                return;
            }

            case 2: {
                Log.i(TAG, "Move Forward to come to center");
                robot.moveForwardToPosition(power, distance);
                return;
            }

            case 3: {
                Log.i(TAG, "Move Right to come to center");
                robot.moveRightToPosition(power, distance); //Move back 2 inches from center
                return;
            }

            case 4: {
                Log.i(TAG, "Move Left to come to center");
                robot.moveLeftToPosition(power, distance); //Move back 2 inches from center
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public int detectOnceNew(org.firstinspires.ftc.teamcode.Robot robot) {
        Log.i(TAG, "Enter FUNC:  detectOnceNew");
        int zigzag_distance = 3;
        double zigzag_power = 0.3;
        for (int i = 0; i < 5; i++) {
            Log.i(TAG, "Iteration # " + i);
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    telemetry.update();
                    Log.i(TAG, "Number of object detected : " + updatedRecognitions.size());
                    if (updatedRecognitions.size() == 1) {
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                Log.i(TAG, "Gold Mineral Found, Lets move it");
                                telemetry.addData("Gold Mineral", "Detected!");
                                telemetry.update();
                                move_center(robot, i, zigzag_distance, zigzag_power);
                                return GOLD_MINERAL_FOUND;
                            } else {
                                Log.i(TAG, "Silver Mineral Found, Move ON");
                                telemetry.addData("Gold Mineral", "Not detected!");
                                telemetry.update();
                                move_center(robot, i, zigzag_distance, zigzag_power);
                                return SILVER_MINERAL_FOUND;
                            }
                        }
                    } else { //More than one object detected, lets move around a little
                        Log.i(TAG, "Number of object detected More than 1");
                        Log.i(TAG, "Move around to see if we can detect in next iteration");
                        telemetry.addData("Detection", "Trying to move around");
                        telemetry.update();
                        ZigZag(robot, i, zigzag_distance, zigzag_power);
                    }
                } else {//Zero object detected, lets try to move around a little
                    Log.i(TAG, "Number of object detected 0");
                    Log.i(TAG, "Move around to see if we can detect in next iteration");
                    telemetry.addData("Detection", "Trying to move around");
                    telemetry.update();
                    ZigZag(robot, i, zigzag_distance, zigzag_power);
                }
            } else { //TFOD is not initailized properly, push the element in front (middle)
                telemetry.addData("Tfod", "Not Initialized!");
                telemetry.update();
                Log.e(TAG, "TFOD Not initialized, returning");
                move_center(robot, i, zigzag_distance, zigzag_power);
                return NO_MINERAL_FOUND;
            }
        }
        Log.i(TAG, "All Iteration Done, No Mineral Found");
        Log.i(TAG, "Exit FUNC: detectOnceNew");
        return NO_MINERAL_FOUND;
    }

    public int detectOnceTime(org.firstinspires.ftc.teamcode.Robot robot) {
        Log.i(TAG, "Enter FUNC:  detectOnceTime");
        for (int i = 0; i < 6; i++) {
            sleep(300);
            Log.i(TAG, "Iteration # " + i);
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    Log.i(TAG, "Number of object detected : " + updatedRecognitions.size());
                    if (updatedRecognitions.size() == 1) {
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                Log.i(TAG, "Gold Mineral Found, Lets move it");
                                telemetry.addData("Gold Mineral", "Detected!");
                                telemetry.update();
                                return GOLD_MINERAL_FOUND;
                            } else {
                                Log.i(TAG, "Silver Mineral Found, Move ON");
                                telemetry.addData("Gold Mineral", "Not detected!");
                                telemetry.update();
                                return SILVER_MINERAL_FOUND;
                            }
                        }
                    } else { //More than one object detected, lets move around a little
                        Log.i(TAG, "Number of object detected More than 1, trying one more time");
                    }
                } else {//Zero object detected, lets try to move around a little
                    Log.i(TAG, "Number of object detected 0, Trying one more time");
                }
            } else { //TFOD is not initailized properly, push the element in front (middle)
                telemetry.addData("Tfod", "Not Initialized!");
                telemetry.update();
                Log.e(TAG, "TFOD Not initialized, returning");
                return NO_MINERAL_FOUND;
            }
        }
        Log.i(TAG, "All Iteration Done, No Mineral Found");
        Log.i(TAG, "Exit FUNC: detectOnceNew");
        return NO_MINERAL_FOUND;
    }


    public void runOpMode() {

        int detect_result;
        initVuforia();

        initTfod();

        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Log.i(TAG, "*************Starting Autonomous TEST**************************");

        long start_time = System.currentTimeMillis();
        long time_taken;
        if (opModeIsActive()) {
            if (tfod != null) {
                tfod.activate();
            }
        }
        //Begin step 1
        //Drop from lander

        Log.i(TAG, "STEP 1: Come Down and Unlatch");
        if (test) {
            robot.unlatchUsingEncoderPosition(1, 1, 12);
            robot.moveRightToPosition(1, 4);
        }
        robot.moveBackwardForTime(0.25, 350, false);
        robot.moveForwardForTime(0.5, 600, true);
        // robot.moveLeftForTime(0.5, 200, false);

        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 1: Completed after : " + time_taken + " milli seconds");
        //End step 1

        /*********************Begin step 2 **************************/

        telemetry.addData("Detection", "Started");
        telemetry.update();
        Log.i(TAG, "STEP 2: Detect and Dislodge ");

            detect_result = detectOnceTime(robot);
        if (detect_result == GOLD_MINERAL_FOUND) {
            Log.i(TAG, "Gold Mineral detected at Center: Knocking off");
            //Knock off mineral
            robot.moveForwardForTime(0.5, 600, true);
            robot.pause(250);
            robot.moveBackwardForTime(0.5, 450, true);
            robot.pause(250);
            robot.turnForTime(-0.7, 875, false, 1);
        } else if (detect_result == NO_MINERAL_FOUND) {
            Log.i(TAG, "Detection Problem at center : Still Knocking off");
            //for time being knock off the mineral
            robot.moveForwardForTime(0.5, 600, true);
            robot.pause(250);
            robot.moveBackwardForTime(0.5, 450, true);
            robot.pause(250);
            robot.turnForTime(-0.7, 875, false, 1);

        } else {//Move right 14.5 in.
            Log.i(TAG, "Silver Mineral Detected at Center: Moving Right");
            robot.pause(250);
            robot.moveRightForTime(0.5, 850, true);
            detect_result = detectOnceTime(robot);

            if (detect_result == GOLD_MINERAL_FOUND) {
                Log.i(TAG, "Gold Mineral detected at Right location: Knocking off");
                //Knock off mineral
                robot.moveForwardForTime(0.5, 550, true);
                robot.pause(250);
                robot.moveBackwardForTime(0.5, 400, true);
                robot.pause(250);
                //Come back to center
                robot.turnForTime(-0.9, 800, false, 1);
                robot.moveBackwardForTime(0.5, 600, true);

            } else if (detect_result == NO_MINERAL_FOUND) {
                Log.i(TAG, "Detection Problem at right location : Still Knocking off");
                //Knock off mineral
                robot.moveForwardForTime(0.5, 550, true);
                robot.pause(250);
                robot.moveBackwardForTime(0.5, 400, true);
                robot.pause(250);
                //Come back to center
                robot.turnForTime(-0.9, 800, false, 1);
                robot.moveBackwardForTime(0.5, 600, true);

            } else { // Knock of Leftmost Mineral
                Log.i(TAG, "Silver Mineral Detected at Right Location : Knocking of Left Mineral");
                //Move left 232 in.


                robot.moveLeftForTime(0.7, 600, true);
                robot.pause(100);
                robot.turnForTime(-0.9, 100, false, 1);
                robot.pause(100);
                robot.moveLeftForTime(0.7, 700, true);
                //Knock off mineral
                robot.moveForwardForTime(0.5, 550, true);
                robot.pause(250);
                robot.moveBackwardForTime(0.5, 400, true);
                robot.pause(250);
                robot.turnForTime(-0.9, 800, false, 1);
                robot.pause(250);
                robot.moveForwardForTime(0.5, 500, true);
                //Come Back to Center
                // robot.moveRightForTime(0.6, 950, true);
            }
        }

        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 2: Completed after : " + time_taken + " Milli Seconds");
        //End step 2
if (test) {

    /*********************Begin step 3 **************************/

    Log.i(TAG, "STEP 3: Drop Team Marker ");

    robot.pause(250);
    robot.moveBackwardForTime(1, 1100, true);
    robot.pause(250);
    robot.turnForTime(0.9, 300, false, 1);
    robot.grabberSlideMoveTime(-1, 1000);

    //robot.wall_align(-0.22, 1800);
    robot.moveLeftForTime(0.3, 2200, false);
    robot.pause(250);
    robot.moveRightForTime(0.3, 500, true);
    robot.moveForwardForTime(1, 700, true);
    robot.grabberRotatorMoveTime(1, 2200);
    robot.releaseMineral(20);
    time_taken = System.currentTimeMillis() - start_time;
    Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");
    robot.moveBackwardForTime(1, 1000, true);
    time_taken = System.currentTimeMillis() - start_time;
    Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");
}

        if (tfod != null) {
            tfod.shutdown();
        }
        Log.i(TAG, "================== Autonomous TEST Finished =======================");
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}
