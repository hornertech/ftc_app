package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.robotUtils.Robot;
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
public class AC extends LinearOpMode {
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

    public void ZigZag(org.firstinspires.ftc.teamcode.RobotUtils robotUtils, int iteration,
                       int distance, double power) {
        int sleeptime = 100;
        switch (iteration) {
            case 0: {
                Log.i(TAG, "ZigZag Moving Forward");
                robotUtils.moveForwardToPosition(power, distance); //Move forward 2 inch from center
                sleep(sleeptime);
                return;
            }

            case 1: {
                Log.i(TAG, "ZigZag Moving Backward");
                robotUtils.moveBackwardToPosition(power, 2 * distance); //Move back 2 inches from center
                sleep(sleeptime);
                return;
            }

            case 2: {
                Log.i(TAG, "ZigZag Moving Left");
                robotUtils.moveForwardToPosition(power, distance); //Come to center
                robotUtils.moveLeftToPosition(power, distance); //Move Left 2 inches from center
                sleep(sleeptime);
                return;
            }

            case 3: {
                Log.i(TAG, "ZigZag Moving Right");
                robotUtils.moveRightToPosition(power, 2 * distance); // Move 2 inches right from center
                sleep(sleeptime);
                return;
            }

            case 4: {
                Log.i(TAG, "ZigZag Coming back to center");
                robotUtils.moveLeftToPosition(power, distance); //Come back to center, detection failed
                sleep(sleeptime);
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public void move_center(org.firstinspires.ftc.teamcode.RobotUtils robotUtils, int iteration,
                            int distance, double power) {
        int sleeptime = 100;
        switch (iteration) {
            case 0: {
                Log.i(TAG, "Already At Center");
                return;
            }

            case 1: {
                Log.i(TAG, "Move Backward to come to center");
                robotUtils.moveBackwardToPosition(power, distance);
                return;
            }

            case 2: {
                Log.i(TAG, "Move Forward to come to center");
                robotUtils.moveForwardToPosition(power, distance);
                return;
            }

            case 3: {
                Log.i(TAG, "Move Right to come to center");
                robotUtils.moveRightToPosition(power, distance); //Move back 2 inches from center
                return;
            }

            case 4: {
                Log.i(TAG, "Move Left to come to center");
                robotUtils.moveLeftToPosition(power, distance); //Move back 2 inches from center
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public int detectOnceNew(org.firstinspires.ftc.teamcode.RobotUtils robotUtils) {
        Log.i(TAG, "Enter FUNC:  detectOnceNew");
        int zigzag_distance = 3;
        double zigzag_power = 0.3;
        for (int i = 0; i < 5; i++) {
            Log.i(TAG, "Iteration # " + i);
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    printTelemetryUpdate("# Object Detected", updatedRecognitions.size()+"");
                    Log.i(TAG, "Number of object detected : " + updatedRecognitions.size());
                    if (updatedRecognitions.size() == 1) {
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                Log.i(TAG, "Gold Mineral Found, Lets move it");
                                printTelemetryUpdate("Gold Mineral", "Detected!");
                                move_center(robotUtils, i, zigzag_distance, zigzag_power);
                                return GOLD_MINERAL_FOUND;
                            } else {
                                Log.i(TAG, "Silver Mineral Found, Move ON");
                                printTelemetryUpdate("Gold Mineral", "Not detected!");
                                move_center(robotUtils, i, zigzag_distance, zigzag_power);
                                return SILVER_MINERAL_FOUND;
                            }
                        }
                    } else { //More than one object detected, lets move around a little
                        Log.i(TAG, "Number of object detected More than 1");
                        Log.i(TAG, "Move around to see if we can detect in next iteration");
                        printTelemetryUpdate("Detection", "Trying to move around");
                        ZigZag(robotUtils, i, zigzag_distance, zigzag_power);
                    }
                } else {//Zero object detected, lets try to move around a little
                    Log.i(TAG, "Number of object detected 0");
                    Log.i(TAG, "Move around to see if we can detect in next iteration");
                    printTelemetryUpdate("Detection", "Trying to move around");
                    ZigZag(robotUtils, i, zigzag_distance, zigzag_power);
                }
            } else { //TFOD is not initailized properly, push the element in front (middle)
                printTelemetryUpdate("Tfod", "Not Initialized!");
                Log.e(TAG, "TFOD Not initialized, returning");
                move_center(robotUtils, i, zigzag_distance, zigzag_power);
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

        org.firstinspires.ftc.teamcode.RobotUtils robotUtils = new org.firstinspires.ftc.teamcode.RobotUtils(hardwareMap, telemetry);

        printTelemetryUpdate(">", "Press Play to start tracking");
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
        Log.i(TAG, "STEP 1: Come Down and Unlatch");

        robotUtils.unlatchUsingEncoderPosition(1, 1, 12);        // Coming Down
        robotUtils.moveRightToPosition(1, 4);                // Unlock
        robotUtils.moveBackwardForTime(0.25, 250, false);    // This is to align the robot with lander
        robotUtils.moveForwardToPosition(0.5, 17);           // Move forward to the center position
        robotUtils.moveLeftToPosition(0.5, 4);

        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 1: Completed after : " + time_taken + " milli seconds");
        //End step 1

        //Begin step 2
        printTelemetryUpdate("Detection", "Started");
        Log.i(TAG, "STEP 2: Detect and Dislodge ");

        detect_result = detectOnceNew(robotUtils);
        if (detect_result == GOLD_MINERAL_FOUND) {
            Log.i(TAG, "Gold Mineral detected at Center: Knocking off");
            //Knock off mineral
            robotUtils.moveForwardToPosition(0.5, 14);
            robotUtils.pause();
            robotUtils.moveBackwardToPosition(0.5, 14);

        } else if (detect_result == NO_MINERAL_FOUND) {
            Log.i(TAG, "Detection Problem at center : Still Knocking off");
            //for time being knock off the mineral
            robotUtils.moveForwardToPosition(0.5, 14);
            robotUtils.pause();
            robotUtils.moveBackwardToPosition(0.5, 14);
        } else {//Move right 14.5 in.
            Log.i(TAG, "Silver Mineral Detected at Center: Moving Right");
            robotUtils.moveRightForTime(0.5, 1000, false);
            detect_result = detectOnceNew(robotUtils);
            if (detect_result == GOLD_MINERAL_FOUND) {
                Log.i(TAG, "Gold Mineral detected at Right location: Knocking off");
                //Knock off mineral
                robotUtils.moveForwardToPosition(0.5, 14);
                robotUtils.pause();
                robotUtils.moveBackwardToPosition(0.5, 14);
                //Come back to center
                robotUtils.moveLeftForTime(0.5, 1000, false);
            } else if (detect_result == NO_MINERAL_FOUND) {
                Log.i(TAG, "Detection Problem at right location : Still Knocking off");
                //Knock off mineral
                robotUtils.moveForwardToPosition(0.5, 14);
                robotUtils.pause();
                robotUtils.moveBackwardToPosition(0.5, 14);
                //Come back to center
                robotUtils.moveLeftForTime(0.5, 1000, false);
            } else { // Knock of Leftmost Mineral
                Log.i(TAG, "Silver Mineral Detected at Right Location : Knocking of Left Mineral");
                //Move left 232 in.
                robotUtils.moveLeftForTime(0.75, 1700, false);
                //Knock off mineral
                robotUtils.moveForwardToPosition(0.5, 14);
                robotUtils.pause();
                robotUtils.moveBackwardToPosition(0.5, 14);

                //Come Back to Center
                robotUtils.moveRightForTime(0.7, 900, false);
            }
        }
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 2: Completed after : " + time_taken + "Milli Seconds");
        //End step 2

        //Begin step 3
        //Add wall alignment here
        Log.i(TAG, "STEP 3: Drop Team Marker ");
        robotUtils.moveLeftForTime(0.8, 2100, false);
        robotUtils.turn(0.6, 1000);
        robotUtils.wall_align(0.2, 1200);
        // robotUtils.pause();
        robotUtils.moveLeftToPosition(0.5, 3);
        //robotUtils.moveForwardToPosition(0.5, 62);
        robotUtils.moveForwardForTime(1, 1400, true);
        sleep(1000);
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 3: Completed after : " + time_taken + "Milli Seconds");
        robotUtils.unlatchUsingEncoderPosition(1, -1, 12);

        Log.i(TAG, "STEP 4: Park At Crater ");
        //Begin Step 4
        robotUtils.moveBackwardToPosition(1, 75);
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 4: Completed after : " + time_taken + "Milli Seconds");
        //End step 4


        if (tfod != null) {
            tfod.shutdown();
        }
        Log.i(TAG, "================== Autonomous TEST Finished =======================");
    }

    /**
     * @param firstVariable
     * @param secondVariable
     */
    public void printTelemetryUpdate(String firstVariable, String secondVariable) {
        telemetry.addData(firstVariable, secondVariable);
        telemetry.update();
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
