package org.firstinspires.ftc.teamcode.subsystems;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.ServoGroup;
import dev.nextftc.hardware.positionable.SetPosition;

public class Pushers implements Subsystem {
    public static final Pushers INSTANCE = new Pushers();
    private Pushers() { }

    //todo Tune these positions on robot
    private static final double EXTENDED_POSITION = 1.0;
    private static final double RETRACTED_POSITION = 0.0;

    //todo Tune timing on robot (seconds)
    private static final double EXTEND_HOLD_S = 0.3;
    private static final double RETRACT_HOLD_S = 0.3;

    private ServoEx leftPusher = new ServoEx("leftPusher");
    private ServoEx rightPusher = new ServoEx("rightPusher");
    private ServoGroup group = new ServoGroup(leftPusher, rightPusher);

    public Command pushOnce() {
        return new SequentialGroup(
                new SetPosition(group, EXTENDED_POSITION),
                new Delay(EXTEND_HOLD_S),
                new SetPosition(group, RETRACTED_POSITION),
                new Delay(RETRACT_HOLD_S)
        );
    }

    public Command extend() {
        return new SetPosition(group, EXTENDED_POSITION);
    }

    public Command retract() {
        return new SetPosition(group, RETRACTED_POSITION);
    }

    @Override
    public void periodic() { }
}
