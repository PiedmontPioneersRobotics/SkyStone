///* Copyright (c) 2017 FIRST. All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without modification,
// * are permitted (subject to the limitations in the disclaimer below) provided that
// * the following conditions are met:
// *
// * Redistributions of source code must retain the above copyright notice, this list
// * of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright notice, this
// * list of conditions and the following disclaimer in the documentation and/or
// * other materials provided with the distribution.
// *
// * Neither the name of FIRST nor the names of its contributors may be used to endorse or
// * promote products derived from this software without specific prior written permission.
// *
// * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
// * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
// * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package org.firstinspires.ftc.teamcode.Graveyard;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.teamcode.Robot;
//
///**
// * This file illustrates the concept of driving a path based on encoder counts.
// * It uses the common Pushbot hardware class to define the drive on the robot.
// * The code is structured as a LinearOpMode
// *
// * The code REQUIRES that you DO have encoders on the wheels,
// *   otherwise you would use: PushbotAutoDriveByTime;
// *
// *  This code ALSO requires that the drive Motors have been configured such that a positive
// *  power command moves them forwards, and causes the encoders to count UP.
// *
// *   The desired path in this example is:
// *   - Drive forward for 48 inches
// *   - Spin right for 12 Inches
// *   - Drive Backwards for 24 inches
// *   - Stop and close the claw.
// *
// *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
// *  that performs the actual movement.
// *  This methods assumes that each movement is relative to the last stopping place.
// *  There are other ways to perform encoder based moves, but this method is probably the simplest.
// *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
// *
// * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
// * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
// */
//
//@Autonomous(name="AutoTurnTest", group="Autonomous")
//@Disabled
//public class AutoTurnTest extends LinearOpMode {
//
//    /* Declare OpMode members. */
//    private Robot robot = new Robot();
//    private ElapsedTime     runtime = new ElapsedTime();
//    static final double     TICKS_PER_ROTATION    = 1120 ;    // eg: TETRIX Motor Encoder
//    static final double     GEAR_RATIO    = 1.0 ;     // This is < 1.0 if geared UP
//    static final double     WHEEL_DIAMETER_CENTIMETERS   = 10.16 ;     // For figuring circumference
//    static final double     COUNTS_PER_CENTIMETER         = (TICKS_PER_ROTATION * GEAR_RATIO) /
//            (WHEEL_DIAMETER_CENTIMETERS * 3.1415);
//    static final double     DRIVE_SPEED             = 0.6;
//    static final double     TURN_SPEED              = 0.5;
//
//    public void runOpMode() {
//        robot.leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
//        robot.rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
//
//        telemetry.addData("Status", "Resetting Encoders");    //
//        telemetry.update();
//
//        robot.leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        robot.rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        // Wait for the game to start (driver presses PLAY)
//        waitForStart();
//
//    }
//    public void EncoderTurn (double speed, double angle) {
//        //make an integer to see how many ticks to rotate
//    }
//}
