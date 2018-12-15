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

        robot.holder.setPower(1);

        while (opModeIsActive()) {
            telemetry.addData("TeleOP", "New Code");
            telemetry.update();
            //Gamepad 1 controls
            float joystickRightX = this.gamepad1.right_stick_x;
            float joystickRightY = this.gamepad1.right_stick_y;
            float joystickLeftX = this.gamepad1.left_stick_x;

            //Gamepad 2 controls
            boolean leftBumper = this.gamepad2.left_bumper;
            float leftTrigger = this.gamepad2.left_trigger;
            boolean rightBumper = this.gamepad2.right_bumper;
            float rightTrigger = this.gamepad2.right_trigger;
            boolean dpadUp = this.gamepad2.dpad_up;
            //Forward, backward, and diagonal movement
            if((joystickRightX*joystickRightX) - (joystickRightY*joystickRightY) < 0){
                if (this.gamepad1.b == true) {
                    if (joystickRightY > 0) {
                        robot.moveFR(1);
                    } else if (joystickRightY < 0) {
                        robot.moveBR(1);
                    }
                } else if (this.gamepad1.x == true) {
                    if (joystickRightY > 0) {
                        robot.moveFL(1);
                    } else if (joystickRightY < 0) {
                        robot.moveBL(1);
                    }
                } else {
                    if (joystickRightY < 0) {
                        robot.moveF(1);
                    } else if (joystickRightY > 0) {
                        robot.moveB(1);
                    }
                }
                //Move left and right
            } else {
                if (joystickRightX > 0) {
                    robot.moveR(1);
                } else if (joystickRightX < 0) {
                    robot.moveL(1);
                }
            }

            //Turning
            if (joystickLeftX != 0) {
                if (joystickLeftX > 0) {
                    robot.slow_turn(-1);
                } else {
                    robot.slow_turn(1);
                }
            }

            //Hanger - move up or down
            if (this.gamepad2.x == true) {
                telemetry.addData("Hanging", "Starting");
                telemetry.update();
                robot.relatch();
                robot.slide(500);
                sleep(2000);
                robot.slide(1850);
                robot.motor_5.setPower(0.25);
                robot.relatch();
                robot.motor_5.setPower(0);
            }

            if (rightTrigger == 1) {
                telemetry.addData("TeleOP", "New Code");
                telemetry.update();
                robot.latch.setPower(1);
                //sleep(1);
                // robot.latch.setPower(0);
            } else if (rightBumper == true) {
                telemetry.addData("TeleOP", "New Code");
                telemetry.update();
                robot.latch.setPower(-1);
                //sleep(1);
                //robot.latch.setPower(0);
            }


            if (leftTrigger == 1) {
                robot.latch.setPower(0);
            } else if (leftBumper == true) {
                robot.holder.setPower(-0.5);
                sleep(1);
                robot.holder.setPower(0);
            }

            if (dpadUp == true) {
                robot.slide(1);
            }
        };
    };
}