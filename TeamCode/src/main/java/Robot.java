package org.firstinspires.ftc.teamcode;

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

    private Gyroscope imu;
    private CRServo grabber_1;
    private CRServo holder;
    private DcMotor motor_0;
    private DcMotor motor_1;
    private DcMotor motor_2;
    private DcMotor motor_3;
    private DcMotor motor_4;
    public DcMotor motor_5;
    private DcMotor motor_6;
    private DcMotor latch;
    private CRServo teammarker;
    public ElapsedTime mRuntime;

    public boolean debugOn = false;

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

    public void moveB (long distance) {
        telemetry.addData("Direction", "Forward");
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
        telemetry.addData("Direction", "Forward");
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
        telemetry.addData("Direction", "Backward");
        telemetry.update();
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
