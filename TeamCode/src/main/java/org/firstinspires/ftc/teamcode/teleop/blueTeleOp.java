package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Pushers;
import org.firstinspires.ftc.teamcode.subsystems.Shooting;
import org.firstinspires.ftc.teamcode.subsystems.flywheel;
import org.firstinspires.ftc.teamcode.subsystems.intake;

import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.DriverControlledCommand;

@TeleOp(name = "BLUE TeleOp")
public class blueTeleOp extends NextFTCOpMode {

    private static final Pose CENTER = new Pose(0, 0, Math.toRadians(0));

    public blueTeleOp() {
        addComponents(
                BulkReadComponent.INSTANCE,
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(flywheel.INSTANCE, intake.INSTANCE, Pushers.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        PedroComponent.follower().setPose(CENTER);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addLine("BLUE TELEOP");
            telemetry.update();
        }
    }

    @Override
    public void onStartButtonPressed() {
        // Field centric driving
        DriverControlledCommand driverControlled = new PedroDriverControlled(
                Gamepads.gamepad1().leftStickY(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX(),
                false
        );
        driverControlled.schedule();

        // Both stick buttons: Reset heading (current facing = new forward)
        Gamepads.gamepad1().leftStickButton().and(Gamepads.gamepad1().rightStickButton())
                .whenBecomesTrue(
                        new LambdaCommand("Reset Heading")
                                .setStart(() -> {
                                    Pose pose = PedroComponent.follower().getPose();
                                    PedroComponent.follower().setPose(new Pose(pose.getX(), pose.getY(), Math.toRadians(0)));
                                })
                                .setIsDone(() -> true)
                );

        // RIGHT BUMPER: Shoot (fixed velocity)
        Gamepads.gamepad1().rightBumper().whenBecomesTrue(
                new LambdaCommand("Shoot")
                        .setStart(() -> Shooting.shoot().schedule())
                        .setIsDone(() -> true)
        );

        // LEFT BUMPER: Toggle intake
        Gamepads.gamepad1().leftBumper().whenBecomesTrue(
                new LambdaCommand("Toggle Intake")
                        .setStart(() -> {
                            if (intake.INSTANCE.isOn()) {
                                intake.INSTANCE.turnOff().schedule();
                            } else {
                                intake.INSTANCE.turnOn().schedule();
                            }
                        })
                        .setIsDone(() -> true)
        );

        // B BUTTON: Emergency stop
        Gamepads.gamepad1().b().whenBecomesTrue(Shooting.emergencyStop());

    }

    @Override
    public void onUpdate() {
        telemetry.addLine("=== SHOOTING ===");
        telemetry.addData("Current Velocity", "%.0f", flywheel.INSTANCE.getCurrentVelocity());
        telemetry.addData("At Speed", flywheel.INSTANCE.isAtSpeed() ? "YES" : "NO");
        telemetry.addLine();
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addLine("LB: Toggle Intake | RB: Shoot");
        telemetry.addLine("B: Emergency Stop");
        telemetry.addData("Intake", intake.INSTANCE.isOn() ? "ON" : "OFF");
        telemetry.update();
    }
}
