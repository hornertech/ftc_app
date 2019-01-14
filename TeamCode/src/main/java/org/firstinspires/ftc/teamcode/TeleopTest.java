package org.firstinspires.ftc.teamcode;
//Fix if detecting 2 or 0 minerals
//Give Power to Servo Motor holder
//Buttons to move latch and slide
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Robot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class TeleopTest extends LinearOpMode{

    @Override
    public void runOpMode()  {
        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot (hardwareMap, telemetry);
        robot.isTeleOp = true;

        telemetry.addData ("Status", "Initialized"); //Displays "Status: Initialized"
        telemetry.update();
        //Driver must press INIT and then ▶️

        waitForStart();
        robot.grabber_rotater.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while (opModeIsActive()) {
            telemetry.addData("TeleOP", "New Code");
            telemetry.update();


            //Turning
            if (this.gamepad1.left_stick_x > 0.5) {
                robot.turnForTime(1, 10, false, 1 );
        }

            if (this.gamepad1.left_stick_x < -0.5) {
                robot.turnForTime(1, 10, false, -1 );
            }


            if (this.gamepad1.right_stick_y > 0.5) {
                telemetry.addData("Grabber-Rotator", "Moving UP");
                telemetry.update();
                robot.moveB(1);
            }

            if (this.gamepad1.right_stick_y < -0.5) {
                telemetry.addData("Grabber-Rotator", "Moving Down");
                telemetry.update();
                robot.moveF(1);
            }

            if (this.gamepad1.right_stick_x > 0.5) {
                telemetry.addData("Grabber-Slide", "Extending");
                telemetry.update();
                robot.moveR(1);
            }

            if (this.gamepad1.right_stick_x < -0.5) {
                telemetry.addData("Grabber-Slide", "Contracting");
                telemetry.update();
                robot.moveL(1);
            }

            if (this.gamepad1.x == true) {
                telemetry.addData("Robot-Testing ", "Moving Left");
                telemetry.update();
                robot.moveLeftForTime(0.3, 2000, true);
            }

            if (this.gamepad1.b == true) {
                telemetry.addData("Robot-Testing ", "Moving Right");
                telemetry.update();
                robot.moveRightForTime(0.3, 2000, true);
            }
            if (this.gamepad1.y == true) {
                telemetry.addData("Robot-Testing ", "Moving Forward");
                telemetry.update();
                robot.moveForwardForTime(1, 1000, true);
            }
            if (this.gamepad1.a == true) {
                telemetry.addData("Robot-Testing ", "Moving Backward");
                telemetry.update();
                robot.moveBackwardForTime(1, 1000, true);
            }

            if (this.gamepad2.x == true) {
                telemetry.addData("Grabber", "Extending");
                telemetry.update();
                robot.turnWithAngleClockwise(0.5, 90);
            }

            if (this.gamepad2.y == true) {
                telemetry.addData("Grabber", "Extending");
                telemetry.update();
                robot.turnWithAngleAnticlockwise(0.5, 90);
            }


            if (this.gamepad2.right_stick_y > 0.5) {
                telemetry.addData("Grabber-Rotator", "Moving UP");
                telemetry.update();
                robot.grabberRotatorMoveTime(1, 50);
            }

            if (this.gamepad2.right_stick_y < -0.5) {
                telemetry.addData("Grabber-Rotator", "Moving Down");
                telemetry.update();
                robot.grabberRotatorMoveTime(-1, 50);
            }

            if (this.gamepad2.left_stick_x > 0.5) {
                telemetry.addData("Grabber-Slide", "Extending");
                telemetry.update();
                robot.grabberSlideMoveTime(1, 50);
            }

            if (this.gamepad2.left_stick_x < -0.5) {
                telemetry.addData("Grabber-Slide", "Contracting");
                telemetry.update();
                robot.grabberSlideMoveTime(-1, 50);
            }

            if (this.gamepad2.left_bumper == true) {
                telemetry.addData("Grabber-Noddles", "Releasing");
                telemetry.update();
                robot.releaseMineral(1);
            }

            if (this.gamepad2.right_bumper == true) {
                telemetry.addData("Grabber-Noddles", "Grabbing");
                telemetry.update();
                robot.grabMineral(1);
            }

            if (this.gamepad1.dpad_up == true) {
                telemetry.addData("Latch", "Extending");
                telemetry.update();
                robot.latch.setPower(1);
                sleep(100);
                robot.latch.setPower(0);
            } else if (this.gamepad1.dpad_down == true) {
                telemetry.addData("Latch", "Contracting");
                telemetry.update();
                robot.latch.setPower(-1);
                sleep(100);
                robot.latch.setPower(0);
            }

        };
    };
}