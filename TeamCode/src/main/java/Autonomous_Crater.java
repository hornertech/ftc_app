package org.firstinspires.ftc.teamcode;

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
public class Autonomous_Crater extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public boolean debugOn = false;
    public boolean test = false;

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

    public void ZigZag(org.firstinspires.ftc.teamcode.Robot robot, int i)
    {
        switch(i){
            case 0: {
                robot.moveF(15); //Move forward 2 inch from center
                sleep(100);
                return;
            }

            case 1: {
                robot.moveB(30); //Move back 2 inches from center
                sleep(100);
                return;
            }

            case 2: {
                robot.moveF(15); //Come to center
                robot.moveL(15); //Move Left 2 inches from center
                sleep(100);
                return;
            }

            case 3: {
                robot.moveR(30); // Move 2 inches right from center
                sleep(100);
                return;
            }

            case 4: {
                robot.moveL(15); //Come back to center, detection failed
                sleep(100);
                return;
            }

            default: {
                // We don't expect this to happen, but return
                return;
            }

        }
    }

    public boolean detectOnceNew(org.firstinspires.ftc.teamcode.Robot robot) {
        for (int i = 0; i < 5; i++) {
            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());
                    telemetry.update();
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
                    } else{ //More than one object detected, lets move around a little
                        telemetry.addData("Detection", "Trying to move around");
                        telemetry.update();
                        ZigZag(robot, i);
                    }
                } else { //Zero object detected, lets try to move around a little
                    telemetry.addData("Detection", "Trying to move around");
                    telemetry.update();
                    ZigZag(robot, i);
                }
            } else { //TFOD is not initailized properly, push the element in front (middle)
                telemetry.addData("Tfod", "Not Initialized!");
                telemetry.update();
                return true;
            }
        }
        return true;
    }

    public void runOpMode() {
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
    if (detectOnce() == true) {
        //Knock off mineral
        robot.moveF(270);
        robot.pause();
        robot.moveB(200);
        if (debugOn == true) sleep(5000);
    } else {
        if (debugOn == true) sleep(5000);
        //Move right 14.5 in.
        robot.moveR(500);

        if (detectOnce() == true) {
            //Knock off mineral
            robot.moveF(270);
            robot.pause();
            robot.moveB(270);
            if (debugOn == true) sleep(5000);
            robot.moveL(500);
            robot.moveB(100);
        } else {
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
    robot.turn(1,300);
   // robot.wall_align(0.25);
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
        robot.moveF(1350);
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
