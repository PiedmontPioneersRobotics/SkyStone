/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="MecanumDrive", group="Linear Opmode")
public class MecanumDrive extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Robot robot = new Robot();
    private int fineTune = 1;




    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        robot.leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        robot.rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if (gamepad1.b || gamepad2.b){
                if (fineTune == 3){
                    fineTune = 1;
                }
                else {
                    fineTune = 3;
                }
            }
            if (gamepad1.dpad_down) {

            }
            // Setup a variable for each drive wheel to save power level for telemetry
            //Range.clip((()/fineTune), -1, 1)
            double r = Math.hypot(gamepad1.left_stick_x, -gamepad1.left_stick_y);
            double robotAngle = Math.atan2(-gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI/4;
            double rightX = gamepad1.right_stick_x;
            final double leftFrontPower = Range.clip(((r * Math.cos(robotAngle) + rightX)/fineTune), -1, 1);
            final double rightFrontPower = Range.clip(((r * Math.sin(robotAngle) - rightX)/fineTune), -1, 1);
            final double leftBackPower = Range.clip(((r * Math.sin(robotAngle) + rightX)/fineTune), -1, 1);
            final double rightBackPower = Range.clip(((r * Math.cos(robotAngle) - rightX)/fineTune), -1, 1);
            robot.leftFront.setPower(leftFrontPower);
            robot.rightFront.setPower(rightFrontPower);
            robot.leftBack.setPower(leftBackPower);
            robot.rightBack.setPower(rightBackPower);
            if(gamepad2.left_trigger != 0) {
                robot.grabber.setPower(0.6/fineTune);
            } else if (gamepad2.right_trigger !=0){
                robot.grabber.setPower(-0.6/fineTune);
            } else {
                robot.grabber.setPower(0);
            }

            if(gamepad2.left_bumper == true){
                robot.leftLifter.setPower(0.2/fineTune);
                robot.rightLifter.setPower(0.2/fineTune);
            } else if(gamepad2.right_bumper == true){
                robot.leftLifter.setPower(-0.2/fineTune);
                robot.rightLifter.setPower(-0.2/fineTune);
            } else{
                robot.leftLifter.setPower(0);
                robot.rightLifter.setPower(0);
            }


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "leftFront (%.2f), rightFront (%.2f),", robot.leftFront.getPower(), robot.rightFront.getPower());
            telemetry.addData("Motors", "leftBack (%.2f), rightBack (%.2f),", robot.leftBack.getPower(), robot.rightBack.getPower());
            telemetry.addData("left front wheel", robot.leftFront.getCurrentPosition());
            telemetry.addData("right front wheel", robot.rightFront.getCurrentPosition());
            telemetry.addData("left back wheel", robot.leftBack.getCurrentPosition());
            telemetry.addData("right back wheel", robot.rightBack.getCurrentPosition());
            telemetry.update();
        }
    }
}