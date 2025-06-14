package com.exosomnia.exoskills;

import com.exosomnia.exolib.recipes.brewing.BrewingRecipeHelper;
import com.exosomnia.exoskills.rendering.RenderingManager;
import com.exosomnia.exoskills.skill.SkillManager;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExoSkills.MODID)
public class ExoSkills
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exoskills";
    public static final SkillManager SKILL_MANAGER = new SkillManager();
    public static final RenderingManager RENDER_MANAGER = new RenderingManager();

    public ExoSkills()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.registerObjects(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(SKILL_MANAGER);
        MinecraftForge.EVENT_BUS.addListener(Registry::registerCommands);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(RENDER_MANAGER);
            MinecraftForge.EVENT_BUS.addListener(this::clientTick);
        });
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Registry.populateAfterRegistration();
        if (ModList.get().isLoaded("irons_spellbooks")) {
            Registry.handleIntegrations(Registry.Integrations.IRONS_SPELLBOOKS);
        }
        if (ModList.get().isLoaded("puffish_attributes")) {
            Registry.handleIntegrations(Registry.Integrations.PUFFISH_ATTRIBUTES);
        }
        if (ModList.get().isLoaded("lootr")) {
            Registry.handleIntegrations(Registry.Integrations.LOOTR);
        }

        event.enqueueWork(() -> {
            BrewingRecipeHelper.addSimplePotionRecipe(Potions.AWKWARD, Registry.ITEM_LUCKY_ESSENCE.get(), Potions.LUCK);
            BrewingRecipeHelper.addSimplePotionRecipe(Potions.LUCK, Registry.ITEM_LUCKY_CLOVER.get(), Registry.POTION_ENHANCED_LUCK.get());
            BrewingRecipeHelper.addSimplePotionRecipe(Registry.POTION_ENHANCED_LUCK.get(), Registry.ITEM_LUCKY_GEM.get(), Registry.POTION_ENHANCED_LUCK_AMPLIFIED.get());
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick(final TickEvent.ClientTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START)) return;
        RENDER_MANAGER.tick();
    }
}
