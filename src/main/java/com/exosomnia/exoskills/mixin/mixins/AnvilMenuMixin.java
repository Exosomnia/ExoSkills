package com.exosomnia.exoskills.mixin.mixins;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.AnvilExpertSkill;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    private DataSlot cost;

    public AnvilMenuMixin(@Nullable MenuType<?> p_39773_, int p_39774_, Inventory p_39775_, ContainerLevelAccess p_39776_) {
        super(p_39773_, p_39774_, p_39775_, p_39776_);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 5, shift = At.Shift.AFTER))
    private void injectAnvilMaster(CallbackInfo ci) {
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        Skills anvilExpert = Skills.ANVIL_EXPERT;
        if (!playerSkillData.hasSkill(anvilExpert)) return;

        AnvilExpertSkill skill = ((AnvilExpertSkill)anvilExpert.getSkill());
        double reduction = skill.costReductionAmountForRank(playerSkillData.getSkillRank(anvilExpert));
        int max = Math.max(0, skill.costReductionMaxForRank(playerSkillData.getSkillRank(anvilExpert)));
        int originalCost = cost.get();
        int actualReduction = (int)Math.min(max, (1.0 - reduction) * originalCost);
        cost.set(originalCost - actualReduction);
    }
}
