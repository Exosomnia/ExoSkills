package com.exosomnia.exoskills.item;

import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exoarmory.items.abilities.FrigidFlurryAbility;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.exosomnia.exoskills.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class BoonItem extends Item {

    public enum BoonAttributes {
        HEALTH("husbandry", UUID.fromString("c4ce0d34-25cc-42d8-a9cc-ebc95a7017c6"), () -> Attributes.MAX_HEALTH, 2, AttributeModifier.Operation.ADDITION),
        ARMOR("husbandry", UUID.fromString("16e62b3d-a188-4468-b365-600d43909619"), () -> Attributes.ARMOR, 1.0, AttributeModifier.Operation.ADDITION),
        ARMOR_TOUGHNESS("husbandry", UUID.fromString("bd490695-1ea3-443d-9836-9da69fec964e"), () -> Attributes.ARMOR_TOUGHNESS, 1.0, AttributeModifier.Operation.ADDITION),
        ATTACK_DAMAGE("mining", UUID.fromString("13d3b66e-a2e6-40bd-ab4c-51bf275fefaa"), () -> Attributes.ATTACK_DAMAGE, .025, AttributeModifier.Operation.MULTIPLY_TOTAL),
        RANGED_STRENGTH("mining", UUID.fromString("03493fac-3cc6-43f8-a6a6-34a08381d4e0"), () -> ExoArmory.REGISTRY.ATTRIBUTE_RANGED_STRENGTH.get(), .025, AttributeModifier.Operation.MULTIPLY_BASE),
        SPELL_DAMAGE("mining", UUID.fromString("cb9e0aa3-8e01-4b20-b976-a30a8e14c973"), () -> Registry.ATTRIBUTE_MAGIC_DAMAGE, .025, AttributeModifier.Operation.MULTIPLY_BASE),
        MOVEMENT_SPEED("fishing", UUID.fromString("4227c21c-5998-4687-b8bd-62d9ed8b9b91"), () -> Attributes.MOVEMENT_SPEED, .025, AttributeModifier.Operation.MULTIPLY_BASE),
        HEALING_RECEIVED("fishing", UUID.fromString("a9c2944f-b03f-45d9-8ce6-8bf2369546c6"), () -> Registry.ATTRIBUTE_STAMINA, .0625, AttributeModifier.Operation.MULTIPLY_BASE),
        LUCK("fishing", UUID.fromString("a7afa64d-32aa-42d2-89bf-022c756dcaa0"), () -> Attributes.LUCK, .5, AttributeModifier.Operation.ADDITION);

        String boonOrigin;
        Supplier<Attribute> attribute;
        AttributeModifier attributeMod;

        BoonAttributes(String boonOrigin, UUID uuid, Supplier<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
            this.boonOrigin = boonOrigin;
            this.attribute = attribute;
            this.attributeMod = new AttributeModifier(uuid, "boon_bonus", amount, operation);
        }
    }

    BoonAttributes boonAttributes;

    public BoonItem(BoonAttributes boonAttributes) {
        super(new Properties().rarity(Rarity.RARE));
        this.boonAttributes = boonAttributes;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) { return true; }

    @Override
    public Component getName(ItemStack itemStack) {
        return Component.translatable(String.format("item.exoskills.boon.%s", boonAttributes.boonOrigin))
                .append(Component.literal(I18n.get("item.exoskills.boon.attribute_info", I18n.get(boonAttributes.attribute.get().getDescriptionId()))));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.boon.info.1", I18n.get(boonAttributes.attribute.get().getDescriptionId())),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));

        //Additional logic for second line. Since the operation can be either addition or multiply, inject a % sign if needed.
        String textLine;
        if (!boonAttributes.attributeMod.getOperation().equals(AttributeModifier.Operation.ADDITION)) {
            textLine = I18n.get("item.exoskills.boon.info.2", boonAttributes.attributeMod.getAmount() * 100);
            textLine = new StringBuilder(textLine).insert(textLine.length() - 1, '%').toString();
        }
        else {
            textLine = I18n.get("item.exoskills.boon.info.2", boonAttributes.attributeMod.getAmount());
        }
        components.add(ComponentUtils.formatLine(textLine, ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));

        components.add(ComponentUtils.formatLine(I18n.get("item.exoskills.boon.info.3"), ComponentUtils.Styles.DEFAULT_DESC.getStyle().withUnderlined(true)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.consume(itemStack); }

        AttributeInstance boonAttribute = player.getAttribute(boonAttributes.attribute.get());
        if (!boonAttribute.hasModifier(boonAttributes.attributeMod)) {
            itemStack.shrink(1);
            boonAttribute.addPermanentModifier(boonAttributes.attributeMod);
            player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.75F);
            player.sendSystemMessage(Component.translatable("item.exoskills.boon.use").withStyle(ChatFormatting.GOLD));
            return InteractionResultHolder.consume(itemStack);
        }

        player.sendSystemMessage(Component.translatable("item.exoskills.boon.use.already_used").withStyle(ChatFormatting.RED));
        return InteractionResultHolder.fail(itemStack);
    }
}
