package org.firstinspires.ftc.teamcode;

//
//
//
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
public class Autonomous_Depot extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    public boolean debugOn = false;



    public int detectionCount = 0;

    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    /**
     * Detection Code
     * @return
     */
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
                    }
                }
            }
        }
        return false;
    }

    /**
     * Main Mathod
     */
    public void runOpMode() {
        initVuforia();

        initTfod();

        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot (hardwareMap, telemetry);
        //robot.isTeleOp = false;
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

        robot.slide(250);//moves litle bit up
        robot.unlatch(); //Unlatch
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
        robot.moveF(350);
        robot.moveR(100);
        //End step 1

        //Begin step 2
        telemetry.addData("Detection", "Started");
        telemetry.update();
        if (detectOnce() == true) {
            //Knock off mineral
            robot.moveF(400);
            robot.pause(250);
            robot.moveB(255);
            if (debugOn == true) sleep(5000);
        } else {
            if (debugOn == true) sleep(5000);
            //Move right 14.5 in.
            robot.moveR(500);

            // This is tempprary printing
            robot.pause(500);
            telemetry.addData("Wating here.....", "Started");
            telemetry.update();

            if (detectOnce() == true) {
                //Knock off mineral
                robot.moveF(350);
                robot.pause(250);
                robot.moveB(400);
                if (debugOn == true) sleep(5000);
                robot.moveL(500);
                robot.moveB(25);
            } else {
                if (debugOn == true) sleep(5000);
                //Move left 29 in.
                robot.moveL(1350);
                //Knock off mineral
                robot.moveF(200);
                robot.pause(800);
                robot.moveB(250);
                robot.moveR(645);
                robot.moveB(60);
            }
        }
        //End step 2

        //Begin step 3
        sleep(1000);
        robot.moveL(1700);     //Move left Side
        robot.turn(1,-60);       //Turn anti-clock wise to align with wall
        robot.wall_align(-0.25);
        robot.pause(250);
        robot.moveR(100);     //Move away from wall
        robot.moveF(850);    //Go Forward
        robot.pause(1000);
        robot.drop_marker();  //Drop the Marker
        robot.pause(500);
        //End Step 3

        //Begin Step 4
        robot.moveB(1150);      //Return to the final Stopping position

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
