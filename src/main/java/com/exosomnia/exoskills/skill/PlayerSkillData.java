package com.exosomnia.exoskills.skill;

import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataWrapper;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class PlayerSkillData implements PersistentPlayerDataWrapper {

    CompoundTag originalTag;
    BitSet unlockedSkills;
    ArrayList<Byte> skillRanks;

    public PlayerSkillData(CompoundTag originalTag) {
        this.originalTag = originalTag;
        unlockedSkills = BitSet.valueOf(originalTag.getLongArray("UnlockedSkills"));
        skillRanks = new ArrayList<>(Collections.nCopies(SkillManager.SKILL_COUNT, (byte)0));
        byte[] originalArray = originalTag.getByteArray("SkillRanks");
        for (var i = 0; i < originalArray.length; i++) {
            skillRanks.set(i, originalArray[i]);
        }
    }

    public boolean addSkill(Skills skill) {
        if (hasSkill(skill)) return false;
        unlockedSkills.set(skill.getShift());
        return true;
    }

    public boolean removeSkill(Skills skill) {
        if (!hasSkill(skill)) return false;
        unlockedSkills.set(skill.getShift(), false);
        return true;
    }

    public boolean increaseSkillRank(Skills skill, byte amount) {
        int shift = skill.getShift();
        skillRanks.set(shift, (byte)(skillRanks.get(shift) + amount));
        return true;
    }

    public boolean reduceSkillRank(Skills skill, byte amount) {
        int shift = skill.getShift();
        skillRanks.set(shift, (byte)Math.max(skillRanks.get(shift) - amount, 0));
        return true;
    }

    public boolean setSkillRank(Skills skill, byte rank) {
        int shift = skill.getShift();
        skillRanks.set(shift, rank);
        return true;
    }

    public byte getSkillRank(Skills skill) {
        int shift = skill.getShift();
        if (skillRanks.size() <= skill.getShift()) return 0;
        return skillRanks.get(shift);
    }

    public boolean hasSkill(Skills skill) {
        return unlockedSkills.get(skill.getShift());
    }

    @Override
    public void serialize(IPersistentPlayerDataStorage iPersistentPlayerDataStorage) {
        originalTag.putLongArray("UnlockedSkills", unlockedSkills.toLongArray());
        originalTag.putByteArray("SkillRanks", skillRanks);
    }
}
