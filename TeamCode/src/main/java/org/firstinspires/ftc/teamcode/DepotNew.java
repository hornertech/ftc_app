package org.firstinspires.ftc.teamcode;


import android.util.Log;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DigitalChannel;
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


@Autonomous(name = "Sensor: Digital touch", group = "Sensor")
public class DepotNew extends LinearOpMode {




    DigitalChannel digitalTouch;  // Hardware Device Object
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";


    private static final int GOLD_MINERAL_FOUND = 0;
    private static final int SILVER_MINERAL_FOUND = 1;
    private static final int NO_MINERAL_FOUND = 2;




    public boolean debugOn = false;
    public boolean test = false;
    public boolean sensortouch = true;
    public String TAG = "FTC_APP";




    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";


    private VuforiaLocalizer vuforia;


    private TFObjectDetector tfod;




    public int detectOnceTime(org.firstinspires.ftc.teamcode.Robot robot) {
        Log.i(TAG, "Enter FUNC:  detectOnceTime");
        for (int i = 0; i < 5; i++) {
            sleep(300);
            Log.i(TAG, "Iteration # " + i);
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    Log.i(TAG, "Number of object detected : " + updatedRecognitions.size());
                    if (updatedRecognitions.size() == 1) {
                        if (i == 0)
                        {
                            Log.i(TAG, "Mineral ditected in first try, let's try one more time");


                        }
                        else {
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
    @Override
    public void runOpMode() {
        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);
        robot.isTeleOp = true;
        // get a reference to our digitalTouch object.
        digitalTouch = hardwareMap.get(DigitalChannel.class, "sensor_digital");


        // set the digital channel to input.
        digitalTouch.setMode(DigitalChannel.Mode.INPUT);


        // wait for the start button to be pressed.
        waitForStart();


        // while the op mode is active, loop and read the light levels.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method
        int detect_result;
        initVuforia();


        initTfod();


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
        //Begin step 1  - Drop from lander
        Log.i(TAG, "STEP 1: Come Down and Unlatch");


        robot.unlatchUsingEncoderPosition(1, 1, 12);        //Unlatching
        robot.moveRightToPosition(1, 4);                    //Un-hook


        robot.moveBackwardForTime(0.25, 350, false);        //Aligning against lander
        robot.moveForwardForTime(1, 350, true);             //Move forward to Central Position
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
            robot.moveForwardForTime(0.5, 550, true);       //Knock off mineral
            robot.pause(100);
            robot.moveBackwardForTime(0.5, 400, true);      //Move backward to Central Position
            robot.pause(100);
            robot.turnWithAngleClockwise(0.5, 90);          //Turn Right
            robot.pause(100);
            robot.moveBackwardForTime(1, 1100, true);
            /*********************Begin step 3 **************************/


            Log.i(TAG, "STEP 3: Drop Team Marker ");
            //Moving back
            robot.pause(100);
            robot.turnWithAngleAnticlockwise(0.5, 55);          //Turn left side
            robot.grabberSlideMoveTime(-1, 1000);               //Extend Slide little bit up
            while (sensortouch==false) {                                      //Wall Allignment


                // send the info back to driver station using telemetry function.
                // if the digital channel returns true it's HIGH and the button is unpressed.
                if (digitalTouch.getState() == true) {
                    telemetry.addData("Digital Touch", "Is Not Pressed");
                    robot.moveLeftForTime(0.3, 600, false);
                } else {
                    robot.pause(100);
                    telemetry.addData("Digital Touch", "Is Pressed");
                    robot.moveRightForTime(0.6, 200, true);
                    robot.moveForwardAndDropSlide(1, 700, true);
                    sensortouch = true;
                }
                telemetry.update();
            }
            //Coming out after aligning
            //    robot.turnWithAngleClockwise(0.5, 10);
            robot.grabberRotatorMoveTime(1, 1500);              //Bring grabber down
            robot.releaseMineral(8);                           //Drop Teammarker
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");


            /*********************Begin step 4 **************************/
            Log.i(TAG, "STEP 4: Park At Crater ");
            robot.moveBackwardForTime(1, 1000, true);
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");


        } else {//Move right 14.5 in.
            Log.i(TAG, "Silver Mineral Detected at Center: Moving Right");
            robot.pause(100);
            robot.moveRightForTime(0.5, 850, true);         //Move Right to look for Mineral at 2nd location
            detect_result = detectOnceTime(robot);
            if (detect_result == GOLD_MINERAL_FOUND) {
                Log.i(TAG, "Gold Mineral detected at Right location: Knocking off");
                //Knock off mineral
                robot.moveForwardForTime(0.5, 550, true);   //Knock off mineral
                //robot.pause(100);
                robot.moveBackwardForTime(0.5, 375, true);  //Move back
                robot.pause(100);
                robot.turnWithAngleClockwise(0.5, 90);      //Turn Right 90 degress
                // robot.moveBackwardForTime(1, 600, true);    //Move back to Central Position
                // robot.pause(100);
                robot.moveBackwardAndExtendSlide(1, -0.5,1700, true);
                /*********************Begin step 3 **************************/


                Log.i(TAG, "STEP 3: Drop Team Marker ");
                //Moving back
                robot.pause(100);
                robot.turnWithAngleAnticlockwise(1, 55);          //Turn left side
                //robot.grabberSlideMoveTime(-1, 900);               //Extend Slide little bit up
                while (sensortouch==false) {                                      //Wall Allignment


                    // send the info back to driver station using telemetry function.
                    // if the digital channel returns true it's HIGH and the button is unpressed.
                    if (digitalTouch.getState() == true) {
                        telemetry.addData("Digital Touch", "Is Not Pressed");
                        robot.moveLeftForTime(0.3, 600, false);
                    } else {
                        robot.pause(100);
                        telemetry.addData("Digital Touch", "Is Pressed");
                        robot.moveRightForTime(0.6, 200, true);
                        robot.moveForwardAndDropSlide(1, 700, true);
                        sensortouch = true;
                    }
                    telemetry.update();
                }                                                                             //Coming out after aligning
                // Move forward to go and drop team marker
                robot.turnWithAngleAnticlockwise(1, 5);
                robot.grabberRotatorMoveTime(1, 1200);              //Bring grabber down
                robot.releaseMineral(8);                           //Drop Teammarker
                //  robot.turnWithAngleClockwise(1, 5);
                time_taken = System.currentTimeMillis() - start_time;
                Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");


                /*********************Begin step 4 **************************/
                Log.i(TAG, "STEP 4: Park At Crater ");
                robot.moveBackwardForTime(1, 1000, true);
                time_taken = System.currentTimeMillis() - start_time;
                Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");
            } else { // Knock of Leftmost Mineral
                Log.i(TAG, "Silver Mineral Detected at Right Location : Knocking of Left Mineral");
                //Move left 232 in.
                robot.moveLeftForTime(0.7, 750, true);      //Move Left to look for Mineral at 3rd location
                robot.pause(100);
                robot.turnWithAngleClockwise(0.5, 15);      //Turn right slighly to align
                robot.pause(100);
                robot.moveLeftForTime(0.7, 750, true);      //Move further Left to look for Mineral at 3rd location
                robot.moveForwardAndDropSlide(0.3, 2300, true);   //Knock off mineral
                robot.turnWithAngleClockwise(0.5, 15);
                //  robot.grabberRotatorMoveTime(1, 1000);
                robot.releaseMineral(10);
                robot.moveBackwardForTime(1, 100, true);
                robot.pause(100);
                robot.turnWithAngleClockwise(0.5, 15);
                while (sensortouch==false) {                                      //Wall Allignment


                    // send the info back to driver station using telemetry function.
                    // if the digital channel returns true it's HIGH and the button is unpressed.
                    if (digitalTouch.getState() == true) {
                        telemetry.addData("Digital Touch", "Is Not Pressed");
                        robot.moveLeftForTime(0.3, 600, false);
                    } else {
                        robot.pause(100);
                        telemetry.addData("Digital Touch", "Is Pressed");
                        robot.moveRightForTime(0.6, 200, true);
                        robot.moveForwardAndDropSlide(1, 700, true);
                        sensortouch = true;
                    }
                    telemetry.update();
                }                                                               //Coming out after aligning
                robot.moveBackwardForTime(1, 1200, true);
                time_taken = System.currentTimeMillis() - start_time;
                Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");




            }
        }
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 2: Completed after : " + time_taken + " Milli Seconds");
        //End step 2






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
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;


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