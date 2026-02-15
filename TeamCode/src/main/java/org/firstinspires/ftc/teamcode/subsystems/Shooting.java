package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.ParallelRaceGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;

public class Shooting {

    //todo Tune this velocity on robot
    private static final double SHOOT_VELOCITY = 1000;

    public static Command shoot() {
        return new SequentialGroup(
                // Spin up flywheel (with 2s timeout)
                new ParallelRaceGroup(
                        flywheel.INSTANCE.runUntilAtSpeed(SHOOT_VELOCITY),
                        new Delay(2.0)
                ),

                // Feed 3 balls while maintaining flywheel speed
                new ParallelRaceGroup(
                        flywheel.INSTANCE.runAtVelocity(SHOOT_VELOCITY),
                        new SequentialGroup(
                                Pushers.INSTANCE.pushOnce(),
                                Pushers.INSTANCE.pushOnce(),
                                Pushers.INSTANCE.pushOnce()
                        )
                ),

                // Stop flywheel
                flywheel.INSTANCE.stop()
        );
    }



    @Deprecated
    public static Command autoShoot(double velocity) {
        return shoot();
    }


    public static Command emergencyStop() {
        return new ParallelGroup(
                flywheel.INSTANCE.stop(),
                intake.INSTANCE.turnOff(),
                Pushers.INSTANCE.retract()
        );
    }
}
