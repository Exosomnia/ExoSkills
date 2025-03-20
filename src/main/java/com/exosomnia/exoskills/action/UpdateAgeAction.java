package com.exosomnia.exoskills.action;

import com.exosomnia.exolib.scheduler.actions.ScheduledAction;
import net.minecraft.world.entity.AgeableMob;
import org.jetbrains.annotations.Nullable;

public class UpdateAgeAction extends ScheduledAction {

    AgeableMob child;
    AgeableMob parentA;
    AgeableMob parentB;
    double reduction;

    public UpdateAgeAction(AgeableMob child, @Nullable AgeableMob parentA, @Nullable AgeableMob parentB, double reduction) {
        this.child = child;
        this.parentA = parentA;
        this.parentB = parentB;
        this.reduction = reduction;
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public void action() {
        child.setAge((int)(child.getAge() * reduction));
        if (parentA != null) parentA.setAge((int)(parentA.getAge() * reduction));
        if (parentB != null) parentB.setAge((int)(parentB.getAge() * reduction));
    }
}
