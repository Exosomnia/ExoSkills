package com.exosomnia.exoskills.action;

import com.exosomnia.exolib.scheduler.actions.ScheduledAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;

public class HorizonOfFateAction extends ScheduledAction {

    public ServerPlayer player;
    public int xpLeft;

    public HorizonOfFateAction(ServerPlayer player, int xpLeft) {
        this.player = player;
        this.xpLeft = xpLeft;
    }

    @Override
    public boolean isValid() {
        return xpLeft > 0;
    }

    @Override
    public void action() {
        ExperienceOrb.award(player.serverLevel(), player.position().add(0, 0.5, 0), Math.min(3, xpLeft));
        xpLeft -= 3;
        manager.scheduleAction(this, 2);
    }
}
