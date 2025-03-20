package com.exosomnia.exoskills.skill;

import com.exosomnia.exoskills.skill.base.BaseSkill;
import com.exosomnia.exoskills.skill.type.*;

public enum Skills {
    NATURES_BLESSING(new NaturesBlessingSkill(0)),
    MASTERCHEF(new MasterchefSkill(1)),
    FARMERS_FORTITUDE(new FarmersFortitudeSkill(2)),
    GREEN_THUMB(new GreenThumbSkill(3)),
    EARTHEN_KNOWLEDGE(new EarthenKnowledgeSkill(4)),
    CAREFUL_FOOTING(new SimpleSkill(5)),
    NURTURERS_TOUCH(new NurturersTouchSkill(6)),
    MANTLES_BLESSING(new MantlesBlessingSkill(7)),
    MINERS_COURAGE(new MinersCourageSkill(8)),
    ORE_SENSE(new OreSenseSkill(9)),
    GEOMANCERS_LUCK(new GeomancersLuckSkill(10)),
    PICKAXE_MASTERY(new PickaxeMasterySkill(11)),
    EXPLOSIVES_EXPERT(new ExplosivesExpertSkill(12)),
    OCEANS_BLESSING(new OceansBlessingSkill(13)),
    ARTIFACT_ANGLER(new ArtifactAnglerSkill(14)),
    FORGOTTEN_TREASURES(new ForgottenTreasuresSkill(15)),
    AQUATIC_KNOWLEDGE(new AquaticKnowledgeSkill(16)),
    PATIENCE(new PatienceSkill(17)),
    ENCHANTED_BOBBER(new EnchantedBobberSkill(18)),
    ARCANE_OVERLOAD(new ArcaneOverloadSkill(19)),
    OCCULT_APOTHECARY(new OccultApothecarySkill(20)),
    HORIZON_OF_FATE(new HorizonOfFateSkill(21)),
    EFFECT_MANIPULATOR(new EffectManipulatorSkill(22)),
    ANVIL_EXPERT(new AnvilExpertSkill(23)),
    ENCHANTMENT_DIVINATION(new EnchantmentDivinationSkill(24)),
    LIFE_MENDING(new LifeMendingSkill(25)),
    LOOTERS_LUCK(new LootersLuckSkill(26)),
    DIURNAL_EXPLORATION(new SimpleSkill(27)),
    NOMADIC_KNOWLEDGE(new NomadicKnowledgeSkill(28)),
    EXPERIENCED_EXPLORER(new ExperiencedExplorerSkill(29)),
    EXPERT_RIDER(new ExpertRiderSkill(30));

    private BaseSkill skill;

    Skills(BaseSkill skill) {
        this.skill = skill;
    }

    public BaseSkill getSkill() {
        return this.skill;
    }

    public int getShift() {
        return this.skill.getId();
    }
}