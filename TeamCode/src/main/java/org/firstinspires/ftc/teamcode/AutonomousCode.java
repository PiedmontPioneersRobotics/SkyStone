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
//useless typing
import android.view.View;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file illustrates the concept of driving a path based on Gyro heading and encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that you have a Modern Robotics I2C gyro with the name "gyro"
 *   otherwise you would use: PushbotAutoDriveByEncoder;
 *
 *  This code requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 *  In order to calibrate the Gyro correctly, the robot must remain stationary during calibration.
 *  This is performed when the INIT button is pressed on the Driver Station.
 *  This code assumes that the robot is stationary when the INIT button is pressed.
 *  If this is not the case, then the INIT should be performed again.
 *
 *  Note: in this example, all angles are referenced to the initial coordinate frame set during the
 *  the Gyro Calibration process, or whenever the program issues a resetZAxisIntegrator() call on the Gyro.
 *
 *  The angle of movement/rotation is assumed to be a standardized rotation around the robot Z axis,
 *  which means that a Positive rotation is Counter Clock Wise, looking down on the field.
 *  This is consistent with the FTC field coordinate conventions set out in the document:
 *  ftc_app\doc\tutorial\FTC_FieldCoordinateSystemDefinition.pdf
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="mainAutonomous")
//@Disabled
public class AutonomousCode extends LinearOpMode {

    /* Declare OpMode members. */
    Robot         robot   = new Robot();   // Use a Pushbot's hardware
    ModernRoboticsI2cGyro   gyro    = null;                    // Additional Gyro device

    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // The can/should be tweaked to suite the specific robot drive train.
    static final double     DRIVE_SPEED             = 0.7;     // Nominal speed for better accuracy.
    static final double     TURN_SPEED              = 0.5;     // Nominal half speed for better accuracy.

    static final double     HEADING_THRESHOLD       = 1 ;      // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.15;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_COEFF           = 0.1;   // Larger is more responsive, but also less stable


    @Override
    public void runOpMode() {

        /*
         * Initialize the standard drive system variables.
         * The init() method of the hardware class does most of the work here
         */
        robot.init(hardwareMap);
        gyro = (ModernRoboticsI2cGyro)hardwareMap.gyroSensor.get("gyro");
        /*
        0 is code for the Depot side onto the midline
        1 is code for the Foundation side onto the midline
        2 is code to take 1 block across the bridge, from Depot side
        3 is code to drive 1 meter (For tests)
         */
        telemetry.log().add("Gyro Calibrating. Do Not Move!");
        gyro.calibrate();
        // Wait until the gyro calibration is complete
        while (!isStopRequested() && gyro.isCalibrating())  {
            sleep(50);
        }

        telemetry.log().clear(); telemetry.log().add("Gyro Calibrated. Press Start.");
        telemetry.clear(); telemetry.update();
//useless code
        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Wait for the start button to be pressed
        waitForStart();
        telemetry.log().clear();

//      Change to which version of the autonomous code to run
        int autoNum = 1;
        // 0 - Start facing towards bridge, Parks on tape close to wall
        // 1 - Start facing towards bridge, Parks on tape close to neutral bridge. Works from building site side
        // 2 - Start facing towards bridge, Parks on tape close to neutral bridge. Works from depot side
        // 3 - Start facing toward foundation, Moves foundation, Parks on tape close to neutral bridge
        // 4 - Start facing toward foundation, Moves foundation, Parks on tape close to wall

        if (autoNum == 0) {
            /*
            Drives forward 10 inches, use this for the parking.
            Uses Encoders and Gyro.
             */
//useless comment
            gyroDrive(DRIVE_SPEED, 25, 0);
        } else if (autoNum == 1){
            strafe(DRIVE_SPEED, 20, 0);
            gyroDrive(DRIVE_SPEED, 25, 0);

        } else if (autoNum == 2) {
            strafe(DRIVE_SPEED, -20, 0);
            gyroDrive(DRIVE_SPEED, 25, 0);
        } else if (autoNum == 3){
            //Move foundation, park closer to neutral bridge.
            moveFoundation(false);
            gyroDrive(DRIVE_SPEED, 55, 0);
            moveFoundation(true);
            sleep(100);
            gyroDrive(DRIVE_SPEED, -55, 0);
            gyroTurn(TURN_SPEED, -90);
            gyroDrive(DRIVE_SPEED, 10, -90);
            moveFoundation(false);
            gyroDrive(DRIVE_SPEED, -50, -90);

        }else if (autoNum == 4){
            //Move foundation, park closer to edge
            moveFoundation(false);
            gyroDrive(DRIVE_SPEED, 55, 0);
            moveFoundation(true);
            sleep(100);
            gyroDrive(DRIVE_SPEED, -55, 0);
            gyroTurn(TURN_SPEED, -90);
            gyroDrive(DRIVE_SPEED, 10, -90);
            moveFoundation(false);

            gyroDrive(DRIVE_SPEED, -50, -90);

        }

    }


    /**
     *  Method to drive on a fixed compass bearing (angle), based on encoder counts.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the desired position
     *  2) Driver stops the opmode running.
     *
     * @param speed      Target speed for forward motion.  Should allow for _/- variance for adjusting heading
     * @param distance   Distance (in inches) to move from current position.  Negative distance means move backwards.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */
    public void gyroDrive ( double speed,
                            double distance,
                            double angle) {

        int     newLeftFrontTarget;
        int     newRightFrontTarget;
        int     newRightBackTarget;
        int     newLeftBackTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        // grey
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFront.getCurrentPosition() + moveCounts;
            newRightFrontTarget = robot.rightFront.getCurrentPosition() + moveCounts;
            newLeftBackTarget = robot.leftBack.getCurrentPosition() + moveCounts;
            newRightBackTarget = robot.rightBack.getCurrentPosition() + moveCounts;


            // Set Target and Turn On RUN_TO_POSITION
            robot.leftFront.setTargetPosition(newLeftFrontTarget);
            robot.rightFront.setTargetPosition(newRightFrontTarget);
            robot.leftBack.setTargetPosition(newLeftBackTarget);
            robot.rightBack.setTargetPosition(newRightBackTarget);

            robot.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.leftFront.setPower(speed);
            robot.rightFront.setPower(speed);
            robot.leftBack.setPower(speed);
            robot.rightBack.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.leftFront.isBusy() && robot.rightFront.isBusy() && robot.leftBack.isBusy() && robot.rightBack.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = speed - steer;
                rightSpeed = speed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                robot.leftFront.setPower(leftSpeed);
                robot.rightFront.setPower(rightSpeed);
                robot.leftBack.setPower(leftSpeed);
                robot.rightBack.setPower(rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d",      newLeftFrontTarget,  newRightFrontTarget);
                telemetry.addData("Actual",  "%7d:%7d:%7d:%7d",      robot.leftFront.getCurrentPosition(),
                        robot.rightFront.getCurrentPosition(),
                        robot.leftBack.getCurrentPosition(),
                        robot.rightBack.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            robot.leftFront.setPower(0);
            robot.rightFront.setPower(0);
            robot.leftBack.setPower(0);
            robot.rightBack.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     *  Method to spin on central axis to point in a new direction.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the heading (angle)
     *  2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     // @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */

    public void rampedEncoderlessRunToPosition (double speed, double distance, int direction){
        encoderlessRunToPosition (speed *.25, distance * .25, direction, true, false);
        encoderlessRunToPosition(speed*0.5, distance * 0.25, direction, false, false);
        encoderlessRunToPosition(speed, distance * 0.25, direction, false, false);
        encoderlessRunToPosition(speed * 0.5, distance * 0.25, direction, false, true);

    }
    public void encoderlessRunToPosition (double speed, double distance, int direction, boolean smoothDriveStart, boolean smoothDriveEnd){

        int moveCounts;

        if(smoothDriveStart) {
            robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        robot.rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if (direction == 0){
            //forward/backwards
            robot.leftFront.setPower(speed);
            robot.leftBack.setPower(speed);
            robot.rightFront.setPower(speed);
            robot.rightBack.setPower(speed);
        } else if(direction == 1){
            // ?? Strafe
            robot.leftFront.setPower(-speed);
            robot.leftBack.setPower(speed);
            robot.rightFront.setPower(-speed);
            robot.rightBack.setPower(speed);
        } else if (direction == 2){
            // ?? Strafe
            robot.leftFront.setPower(speed);
            robot.leftBack.setPower(-speed);
            robot.rightFront.setPower(speed);
            robot.rightBack.setPower(-speed);
        }

        moveCounts = (int)(distance * COUNTS_PER_INCH);

        while (opModeIsActive() && findLargestEncoderValue() < moveCounts){
            //wait for motors to reach distance

        }
        if (smoothDriveEnd) {
            robot.leftFront.setPower(0);
            robot.leftBack.setPower(0);
            robot.rightFront.setPower(0);
            robot.rightBack.setPower(0);
        }
    }

    public void smoothRampedEncoderlessRunToPosition (double speed, double distance, int direction){

        int moveCounts;


        robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        robot.rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        moveCounts = (int)(distance * COUNTS_PER_INCH);
        double rampedSpeed = 0.2 * speed;

        while (opModeIsActive() && findLargestEncoderValue() < moveCounts){
            //wait for motors to reach distance

            if(findLargestEncoderValue() + 1000 > moveCounts && rampedSpeed > 0.1 * speed){
                rampedSpeed -= 0.01 * speed;
            } else if(rampedSpeed < speed){
                rampedSpeed += 0.01 * speed;
            }
            if (direction == 0){
                //forward/backwards
                robot.leftFront.setPower(rampedSpeed);
                robot.leftBack.setPower(rampedSpeed);
                robot.rightFront.setPower(rampedSpeed);
                robot.rightBack.setPower(rampedSpeed);
            } else if(direction == 1){
                // ?? Strafe
                robot.leftFront.setPower(-rampedSpeed);
                robot.leftBack.setPower(rampedSpeed);
                robot.rightFront.setPower(rampedSpeed);
                robot.rightBack.setPower(-rampedSpeed);
            } else if (direction == 2){
                // ?? Strafe
                robot.leftFront.setPower(rampedSpeed);
                robot.leftBack.setPower(-rampedSpeed);
                robot.rightFront.setPower(-rampedSpeed);
                robot.rightBack.setPower(rampedSpeed);
            }
            telemetry.addData("Move Counts", moveCounts);
            telemetry.update();

        }

        robot.leftFront.setPower(0);
        robot.leftBack.setPower(0);
        robot.rightFront.setPower(0);
        robot.rightBack.setPower(0);

    }
    public int findLargestEncoderValue(){

         int max = Math.abs(robot.leftBack.getCurrentPosition());
         int encoderHolder = Math.abs(robot.leftFront.getCurrentPosition());


        if (encoderHolder > max){
            max = encoderHolder;
        }
        encoderHolder = Math.abs(robot.rightFront.getCurrentPosition());
        if (encoderHolder > max){
            max = encoderHolder;
        }
        encoderHolder = Math.abs(robot.rightBack.getCurrentPosition());
        if (encoderHolder > max) {
            max = encoderHolder;
        }
        return max;

    }
    public void gyroTurn (  double speed, double angle) {

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && !onHeading(speed, angle, P_TURN_COEFF)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
    }

    /**
     *  Method to obtain & hold a heading for a finite amount of time
     *  Move will stop once the requested time has elapsed
     *
     * @param speed      Desired speed of turn.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     * @param holdTime   Length of time (in seconds) to hold the specified heading.
     */
    public void gyroHold( double speed, double angle, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();

        // keep looping while we have time remaining.
        holdTimer.reset();
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Update telemetry & Allow time for other processes to run.
            onHeading(speed, angle, P_TURN_COEFF);
            telemetry.update();
        }

        // Stop all motion;
        robot.leftFront.setPower(0);
        robot.rightFront.setPower(0);
        robot.leftBack.setPower(0);
        robot.rightBack.setPower(0);
    }

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed     Desired speed of turn.
     * @param angle     Absolute Angle (in Degrees) relative to last gyro reset.
     *                  0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                  If a relative angle is required, add/subtract from current heading.
     * @param PCoeff    Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff) {
        double   error ;
        double   steer ;
        boolean  onTarget = false ;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed  = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }
        else {
            steer = getSteer(error, PCoeff);
            rightSpeed  = speed * steer;
            leftSpeed   = -rightSpeed;
        }

        // Send desired speeds to motors.
        robot.leftFront.setPower(leftSpeed);
        robot.rightFront.setPower(rightSpeed);
        robot.leftBack.setPower(leftSpeed);
        robot.rightBack.setPower(rightSpeed);

        telemetry.addData("LeftFrontSpeed", robot.leftFront.getPower());
        telemetry.addData("LeftBackSpeed", robot.leftBack.getPower());
        telemetry.addData("RightFrontSpeed", robot.rightFront.getPower());
        telemetry.addData("RightBackSpeed", robot.rightBack.getPower());
        // Display it for the driver.
        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     * @param   targetAngle  Desired angle (relative to global reference established at last Gyro Reset).
     * @return  error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     *          +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double targetAngle) {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle + gyro.getIntegratedZValue();
        while (robotError > 180)  robotError -= 360;
        //useless comment
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     * @param error   Error angle in robot relative degrees
     * @param PCoeff  Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }

    public void runGrabbers(long duration, double speed){
//        robot.grabber.setPower(speed);
//        sleep(duration*1000);
//        robot.grabber.setPower(0);
    }

    public void grabBlock(){
    /*
        if(robot.rightLifter.getPosition()==0) {
            robot.leftLifter.setPosition(1);
            robot.rightLifter.setPosition(1);
        }else if(robot.rightLifter.getPosition()==1){
            robot.leftLifter.setPosition(0);
            robot.rightLifter.setPosition(0);
        }

        runGrabbers(1, 0.5);
        */
    }

    public void driveWithoutEncoders (double speed, double distance, double strafeAngle){
        //not currently usefull
        double robotAngle = strafeAngle - Math.PI/4;

        final double leftFrontPower = Range.clip((distance * Math.cos(robotAngle)), -1, 1);
        final double rightFrontPower = Range.clip((distance * Math.sin(robotAngle)), -1, 1);
        final double leftBackPower = Range.clip((distance * Math.sin(robotAngle)), -1, 1);
        final double rightBackPower = Range.clip((distance * Math.cos(robotAngle)), -1, 1);

        robot.leftFront.setPower(leftFrontPower);
        robot.rightFront.setPower(rightFrontPower);
        robot.leftBack.setPower(leftBackPower);
        robot.rightBack.setPower(rightBackPower);

        sleep(((long)(distance/speed) * 1000));

        robot.leftFront.setPower(0);
        robot.rightFront.setPower(0);
        robot.leftBack.setPower(0);
        robot.rightBack.setPower(0);
    }

    public void driveUsingDuration (double speed, double duration, double strafeAngle){

        robot.leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double robotAngle = strafeAngle - Math.PI/4;

        final double leftFrontPower = Range.clip((duration * Math.cos(robotAngle)), -1, 1);
        final double rightFrontPower = Range.clip((duration * Math.sin(robotAngle)), -1, 1);
        final double leftBackPower = Range.clip((duration * Math.sin(robotAngle)), -1, 1);
        final double rightBackPower = Range.clip((duration * Math.cos(robotAngle)), -1, 1);

        robot.leftFront.setPower(leftFrontPower);
        robot.rightFront.setPower(rightFrontPower);
        robot.leftBack.setPower(leftBackPower);
        robot.rightBack.setPower(rightBackPower);

        sleep((long)duration);

        robot.leftFront.setPower(0);
        robot.rightFront.setPower(0);
        robot.leftBack.setPower(0);
        robot.rightBack.setPower(0);
    }



    public void strafe ( double speed,
                            double distance, double angle) {
        int     newLeftFrontTarget;
        int     newLeftBackTarget;
        int     newRightFrontTarget;
        int     newRightBackTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  backSpeed;
        double  frontSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFront.getCurrentPosition() + moveCounts;
            newLeftBackTarget = robot.leftBack.getCurrentPosition() - moveCounts;
            newRightFrontTarget = robot.rightFront.getCurrentPosition() - moveCounts;
            newRightBackTarget = robot.rightBack.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            robot.leftFront.setTargetPosition(newLeftFrontTarget);
            robot.rightFront.setTargetPosition(newRightFrontTarget);
            robot.leftBack.setTargetPosition(newLeftBackTarget);
            robot.rightBack.setTargetPosition(newRightBackTarget);

            robot.leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            speed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.leftFront.setPower(speed);
            robot.rightFront.setPower(-speed);
            robot.leftBack.setPower(-speed);
            robot.rightBack.setPower(speed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.leftFront.isBusy() && robot.rightFront.isBusy() && robot.leftBack.isBusy() && robot.rightBack.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);
                steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                frontSpeed = speed - steer;
                backSpeed = -(speed + steer);

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(backSpeed), Math.abs(frontSpeed));
                if (max > 1.0)
                {
                    backSpeed /= max;
                    frontSpeed /= max;
                }

                robot.leftFront.setPower(frontSpeed);
                robot.rightFront.setPower(frontSpeed);
                robot.leftBack.setPower(backSpeed);
                robot.rightBack.setPower(backSpeed);

                // Display drive status for the driver.
//                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d",      newLeftFrontTarget,  newRightFrontTarget, newLeftBackTarget, newRightBackTarget);
                telemetry.addData("Actual",  "%7d:%7d:%7d:%7d",      robot.leftFront.getCurrentPosition(),
                        robot.rightFront.getCurrentPosition(),
                        robot.leftBack.getCurrentPosition(),
                        robot.rightBack.getCurrentPosition());
//                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            robot.leftFront.setPower(0);
            robot.rightFront.setPower(0);
            robot.leftBack.setPower(0);
            robot.rightBack.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void moveFoundation(boolean grab){
        if (grab) {
            robot.leftFoundation.setPosition(0);
            robot.rightFoundation.setPosition(1);
        } else {
            robot.leftFoundation.setPosition(1);
            robot.rightFoundation.setPosition(0);
        }
    }
}
