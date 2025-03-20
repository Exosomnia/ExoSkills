package com.exosomnia.exoskills.skill;

import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.type.*;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.BitSet;
import java.util.HashMap;

public class SkillManager {

    //Gets the final skill enum and gets its shift to calculate skill count
    protected final static int SKILL_COUNT = Skills.values()[Skills.values().length - 1].getShift() + 1;

    HashMap<Player, PlayerSkillData> playerSkillsData = new HashMap<>();

    public PlayerSkillData getSkillData(Player player) {
        return playerSkillsData.get(player);
    }

    //Checks if the SkillData exists for the player when logging in, and if not, create it.
    @SubscribeEvent
    public void playerSkillDataCreation(PlayerEvent.LoadFromFile event) {
        Player player = event.getEntity();
        player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            CompoundTag dataTag = playerData.get();
            if (!dataTag.contains("SkillData") || !(dataTag.get("SkillData") instanceof CompoundTag)) {
                dataTag.put("SkillData", new CompoundTag());
            }

            CompoundTag skillDataTag = (CompoundTag)dataTag.get("SkillData");
            if (!skillDataTag.contains("UnlockedSkills") || !(skillDataTag.get("UnlockedSkills") instanceof LongArrayTag)) {
                skillDataTag.put("UnlockedSkills", new LongArrayTag(new BitSet(SKILL_COUNT).toLongArray()));
            }
            if (!skillDataTag.contains("SkillRanks") || !(skillDataTag.get("SkillRanks") instanceof ByteArrayTag)) {
                skillDataTag.put("SkillRanks", new ByteArrayTag(new byte[SKILL_COUNT]));
            }

            PlayerSkillData playerSkillData = new PlayerSkillData(skillDataTag);
            playerData.addWrapper(playerSkillData);
            playerSkillsData.put(player, playerSkillData);
        });
    }

    //Removes the data in memory when a player logs out
    @SubscribeEvent
    public void playerSkillDataRemoval(PlayerEvent.PlayerLoggedOutEvent event) {
        playerSkillsData.remove(event.getEntity());
    }

    @SubscribeEvent
    public void playerSkillClear(ServerStoppingEvent event) {
        playerSkillsData.clear();
    }
}
