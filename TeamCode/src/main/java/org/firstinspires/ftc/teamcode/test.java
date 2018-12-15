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
public class test extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final int GOLD_MINERAL_FOUND = 0;
    private static final int SILVER_MINERAL_FOUND = 1;
    private static final int NO_MINERAL_FOUND = 2;


    public boolean debugOn = false;
    public boolean test = false;
    public  String TAG = "FTC_APP";

    public int detectionCount = 0;

    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;
    public boolean detectOnce() {
        while (opModeIsActive()) {
            /* detectionCount ++;
            if (detectionCount > 50) {
                telemetry.addData("Gold Mineral", "Not detected!");
                telemetry.update();
                return false;
            } */
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    if (updatedRecognitions.size() == 1) {
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                telemetry.addData("Gold Mineral", "Detected!");
                                telemetry.update();
                                return true;
                            } else {
                                telemetry.addData("Gold Mineral", "Not detected!");
                                telemetry.update();
                                return false;
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

    public void ZigZag(org.firstinspires.ftc.teamcode.Robot robot, int iteration,
                                                        int distance, double power)
    {
        int sleeptime = 100;
        switch(iteration){
            case 0: {
                Log.i(TAG, "ZigZag Moving Forward");
                robot.moveForward(power, distance); //Move forward 2 inch from center
                sleep(sleeptime);
                return;
            }

            case 1: {
                Log.i(TAG, "ZigZag Moving Backward");
                robot.moveBackward(power, 2*distance); //Move back 2 inches from center
                sleep(sleeptime);
                return;
            }

            case 2: {
                Log.i(TAG, "ZigZag Moving Left");
                robot.moveForward(power, distance); //Come to center
                robot.moveLeft(power, distance); //Move Left 2 inches from center
                sleep(sleeptime);
                return;
            }

            case 3: {
                Log.i(TAG, "ZigZag Moving Right");
                robot.moveRight(power, 2 * distance); // Move 2 inches right from center
                sleep(sleeptime);
                return;
            }

            case 4: {
                Log.i(TAG, "ZigZag Coming back to center");
                robot.moveLeft(power, distance); //Come back to center, detection failed
                sleep(sleeptime);
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public int detectOnceNew(org.firstinspires.ftc.teamcode.Robot robot) {
        Log.i(TAG, "Starting Detection ");
        int zigzag_distance = 2;
        double zigzag_power = 0.3;
        for (int i = 0; i < 5; i++) {
            Log.i(TAG, "Iteration # " + i);
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    telemetry.update();
                    Log.i(TAG, "Number of object detected " + updatedRecognitions.size());
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
                    } else{ //More than one object detected, lets move around a little
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
                return NO_MINERAL_FOUND;
            }
        }
        return NO_MINERAL_FOUND;
    }

    public void runOpMode() {

        int detect_result;
        initVuforia();

        initTfod();

        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot (hardwareMap, telemetry);

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            if (tfod != null) {
                tfod.activate();
            }
        }
        //Begin step 1
        //Drop from lander
if (test) {
    robot.slide(250);//moves litle bit up
    robot.unlatch();//
    sleep(800); //This sleep allows the robot to drop down
    robot.slide(50);
    telemetry.addData("Landed", "");
    telemetry.update();
    telemetry.addData("Drop", "complete");
    telemetry.update();
    sleep(3500); //Prevents robot from starting sensing early
    robot.hold_slide();
    sleep(300);// Lets the servo hold

    //Move out of the latch
    robot.moveL(100);
    robot.moveF(320);
    robot.moveR(150);
    //End step 1

    //Begin step 2
    telemetry.addData("Detection", "Started");
    telemetry.update();

    detect_result = detectOnceNew(robot);
    if (detect_result == GOLD_MINERAL_FOUND) {
        Log.i(TAG, "Gold Mineral detected at Center: Knocking off");
        //Knock off mineral
        robot.moveF(270);
        robot.pause();
        robot.moveB(200);

    } else if (detect_result == NO_MINERAL_FOUND) {
        Log.i(TAG, "Detection Problem at center : Still Knocking off");
        //for time being knock off the mineral
        robot.moveF(270);
        robot.pause();
        robot.moveB(200);
    } else {//Move right 14.5 in.
        Log.i(TAG, "Silver Mineral Detected at Center: Moving Right");
        robot.moveR(500);
        detect_result = detectOnceNew(robot);
        if (detect_result == GOLD_MINERAL_FOUND) {
            Log.i(TAG, "Gold Mineral detected at Right location: Knocking off");
            //Knock off mineral
            robot.moveF(270);
            robot.pause();
            robot.moveB(270);
            //Come back to center
            robot.moveL(500);
            robot.moveB(100);
        } else if (detect_result == NO_MINERAL_FOUND) {
            Log.i(TAG, "Detection Problem at right location : Still Knocking off");
            //Knock off mineral
            robot.moveF(270);
            robot.pause();
            robot.moveB(270);
            //Come back to center
            robot.moveL(500);
            robot.moveB(100);
        } else { // Knock of Leftmost Mineral
            Log.i(TAG, "Silver Mineral Detected at Right Location : Knocking of Left Mineral");
            if (debugOn == true) sleep(5000);
            //Move left 29 in.
            robot.moveL(1350);
            //Knock off mineral
            robot.moveF(270);
            robot.pause();
            robot.moveB(220);
            robot.moveR(645);
            robot.moveB(60);
        }
    }

    //End step 2

    //Begin step 3
    //Add wall alignment here
    sleep(1000);
    robot.moveL(1650);
    robot.turn(300);
    robot.wall_align(0.25);
    robot.pause();
    robot.moveL(130);
    robot.moveF(800);
    robot.pause();
    robot.pause();
    robot.pause();
    robot.pause();
    robot.drop_marker();
    robot.pause();
    robot.pause();
    //End Step 3
}
        //Begin Step 4
        //robot.moveF(1000);
        robot.moveForward(0.5, 48);
        robot.moveRight(0.5, 20);
        robot.moveLeft(0.5, 20);
        robot.moveBackward(0.5, 48);
     //   robot.turnNew(0.5, 90);
        //End step 3

        if (tfod != null) {
            tfod.shutdown();
        }
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
