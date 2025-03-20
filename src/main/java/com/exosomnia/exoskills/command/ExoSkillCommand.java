package com.exosomnia.exoskills.command;

import com.exosomnia.exoskills.ExoSkills;
import com.exosomnia.exoskills.skill.PlayerSkillData;
import com.exosomnia.exoskills.skill.Skills;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class ExoSkillCommand {

    private static final String[] VALID_MODIFY_OPERATIONS = {"increase", "reduce", "set"};

    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(Commands.literal("exoskill")
                .requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("add")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests(ExoSkillCommand::suggestSkill)
                                        .executes(context ->
                                            executeAdd(context.getSource(),
                                                    EntityArgument.getPlayer(context, "player"),
                                                    StringArgumentType.getString(context, "skill")))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests(ExoSkillCommand::suggestSkill)
                                        .executes(context ->
                                                executeRemove(context.getSource(),
                                                        EntityArgument.getPlayer(context, "player"),
                                                        StringArgumentType.getString(context, "skill")))))
                        .then(Commands.literal("modify")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests(ExoSkillCommand::suggestSkill)
                                        .then(Commands.argument("operation", StringArgumentType.word())
                                                .suggests(ExoSkillCommand::suggestOperation)
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context ->
                                                        executeModify(context.getSource(),
                                                                EntityArgument.getPlayer(context, "player"),
                                                                StringArgumentType.getString(context, "skill"),
                                                                StringArgumentType.getString(context, "operation"),
                                                                IntegerArgumentType.getInteger(context, "amount")))))))
                        .then(Commands.literal("list")
                                .executes(context ->
                                        executeList(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"))))
                        .then(Commands.literal("reset")
                                .executes(context ->
                                        executeReset(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"))))));
    }

    private static CompletableFuture<Suggestions> suggestOperation(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (String operation : VALID_MODIFY_OPERATIONS) {
            if (operation.startsWith(builder.getRemaining().toLowerCase())) { builder.suggest(operation); }
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSkill(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (Skills skill : Skills.values()) {
            String skillString = skill.toString().toLowerCase();
            if (skillString.startsWith(builder.getRemaining().toLowerCase())) { builder.suggest(skillString); }
        }
        return builder.buildFuture();
    }

    private static int executeAdd(CommandSourceStack sourceStack, ServerPlayer player, String skill) {
        try {
            String playerName = player.getDisplayName().getString();
            if (ExoSkills.SKILL_MANAGER.getSkillData(player).addSkill(Skills.valueOf(skill.toUpperCase()))) {
                sourceStack.sendSuccess(() -> Component.literal(String.format("Successfully applied the skill %1$s to %2$s", skill, playerName)), false);
                return Command.SINGLE_SUCCESS;
            }
            sourceStack.sendFailure(Component.literal(String.format("%1$s already has the skill %2$s", playerName, skill)));
        }
        catch (IllegalArgumentException exception) { sourceStack.sendFailure(Component.literal(String.format("%s is not a valid skill name", skill))); }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandSourceStack sourceStack, ServerPlayer player, String skill) {
        try {
            String playerName = player.getDisplayName().getString();
            if (ExoSkills.SKILL_MANAGER.getSkillData(player).removeSkill(Skills.valueOf(skill.toUpperCase()))) {
                sourceStack.sendSuccess(() -> Component.literal(String.format("Successfully removed the skill %1$s to %2$s", skill, playerName)), false);
                return Command.SINGLE_SUCCESS;
            }
            sourceStack.sendFailure(Component.literal(String.format("%1$s does not have the skill %2$s", playerName, skill)));
        }
        catch (IllegalArgumentException exception) { sourceStack.sendFailure(Component.literal(String.format("%s is not a valid skill name", skill))); }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeModify(CommandSourceStack sourceStack, ServerPlayer player, String skill, String operation, int amount) {
        try {
            String playerName = player.getDisplayName().getString();
            Skills modifiedSkill = Skills.valueOf(skill.toUpperCase());
            PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
            switch (operation) {
                case "increase" -> playerSkillData.increaseSkillRank(modifiedSkill, (byte)amount);
                case "decrease" -> playerSkillData.reduceSkillRank(modifiedSkill, (byte)amount);
                case "set" -> playerSkillData.setSkillRank(modifiedSkill, (byte)amount);
                default -> sourceStack.sendFailure(Component.literal(String.format("%s is not a valid operation", operation)));

            }
            sourceStack.sendSuccess(() -> Component.literal(String.format("Set %1$s's skill %2$s to rank %3$d", playerName, skill, playerSkillData.getSkillRank(modifiedSkill))), false);
            return Command.SINGLE_SUCCESS;
        }
        catch (IllegalArgumentException exception) { sourceStack.sendFailure(Component.literal(String.format("%s is not a valid skill name", skill))); }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(CommandSourceStack sourceStack, ServerPlayer player) {
        String playerName = player.getDisplayName().getString();
        StringBuilder skillListBuilder = new StringBuilder();
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        for (Skills skill : Skills.values()) {
            if (playerSkillData.hasSkill(skill)) {
                skillListBuilder.append(String.format("%1$s:%2$d ", skill.toString().toLowerCase(), playerSkillData.getSkillRank(skill)));
            }
        }
        sourceStack.sendSuccess(() -> Component.literal(String.format("%1$s's current skills: %2$s", playerName, skillListBuilder)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeReset(CommandSourceStack sourceStack, ServerPlayer player) {
        String playerName = player.getDisplayName().getString();
        PlayerSkillData playerSkillData = ExoSkills.SKILL_MANAGER.getSkillData(player);
        for (Skills skill : Skills.values()) {
            if (playerSkillData.hasSkill(skill)) {
                playerSkillData.setSkillRank(skill, (byte)0);
                playerSkillData.removeSkill(skill);
            }
        }
        sourceStack.sendSuccess(() -> Component.literal(String.format("Reset skill data for %1$s", playerName)), false);
        return Command.SINGLE_SUCCESS;
    }
}
