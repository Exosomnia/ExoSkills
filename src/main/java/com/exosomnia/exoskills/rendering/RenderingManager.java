package com.exosomnia.exoskills.rendering;

import com.exosomnia.exoskills.Registry;
import com.exosomnia.exoskills.rendering.types.BasicLineRenderType;
import com.exosomnia.exoskills.skill.Skills;
import com.exosomnia.exoskills.skill.type.OreSenseSkill;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RenderingManager {

    public enum HighlightType {
        ORE_SENSE,
        DEFINED_CONTAINERS
    }

    private int tick = 0;

    private int highlightDuration = 0;
    private Block highlightBlock;
    private BlockPos[] highlightLocations;
    private HighlightType highlightType;
    private int highlightColor;

    private static final ImmutableMap<Block, Integer> ORE_DISPLAY_COLORS;
    static {
        ORE_DISPLAY_COLORS = ImmutableMap.<Block, Integer>builder()
                .put(Blocks.COAL_ORE, 0xBFBFBF)
                .put(Blocks.DEEPSLATE_COAL_ORE, 0xBFBFBF)
                .put(Blocks.COPPER_ORE, 0xF0B432)
                .put(Blocks.DEEPSLATE_COPPER_ORE, 0xF0B432)
                .put(Blocks.IRON_ORE, 0x997850)
                .put(Blocks.DEEPSLATE_IRON_ORE, 0x997850)
                .put(Blocks.LAPIS_ORE, 0x246CD6)
                .put(Blocks.DEEPSLATE_LAPIS_ORE, 0x246CD6)
                .put(Blocks.GOLD_ORE, 0xCFC04E)
                .put(Blocks.DEEPSLATE_GOLD_ORE, 0xCFC04E)
                .put(Blocks.REDSTONE_ORE, 0xC92C2C)
                .put(Blocks.DEEPSLATE_REDSTONE_ORE, 0xC92C2C)
                .put(Blocks.DIAMOND_ORE, 0x3FBAEB)
                .put(Blocks.DEEPSLATE_DIAMOND_ORE, 0x3FBAEB)
                .put(Blocks.EMERALD_ORE, 0x57A856)
                .put(Blocks.DEEPSLATE_EMERALD_ORE, 0x57A856)
                .put(Blocks.AIR, 0x8E65AB)
                .build();
    }

    public void tick() {
        tick++;
    }

    public void displayOres(Block block, byte rank) {
        int range = ((OreSenseSkill)Skills.ORE_SENSE.getSkill()).rangeForRank(rank);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ClientLevel level = player.clientLevel;
        BlockPos playerPos = player.blockPosition();
        int px = playerPos.getX();
        int py = playerPos.getY();
        int pz = playerPos.getZ();

        List<BlockPos> foundBlocks = new ArrayList<>();
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    searchPos.set(px+dx, py+dy, pz+dz);
                    if (level.getBlockState(searchPos).is(block)) {
                        foundBlocks.add(searchPos.immutable());
                    }
                }
            }
        }

        this.highlightBlock = block;
        this.highlightLocations = foundBlocks.toArray(new BlockPos[0]);
        this.highlightDuration = tick + ((OreSenseSkill)Skills.ORE_SENSE.getSkill()).durationForRank(rank);
        this.highlightType = HighlightType.ORE_SENSE;
    }

    public void displayPositions(HighlightType type, BlockPos[] positions, int color) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        this.highlightLocations = positions;
        this.highlightDuration = tick + 120;
        this.highlightType = type;
        this.highlightColor = color;
    }

    private boolean isVaildLootContainer(LocalPlayer player, BlockEntity blockEntity) {
        if (!(blockEntity instanceof RandomizableContainerBlockEntity randomContainerEntity)) return false;

        boolean lootrValid = true;
        if (Registry.isLoaded(Registry.Integrations.LOOTR) && (randomContainerEntity instanceof ILootBlockEntity lootrBlockEntity)) {
            lootrValid = !lootrBlockEntity.getOpeners().contains(player.getUUID());
        }
        return lootrValid;
    }

    @SubscribeEvent
    public void worldRender(RenderLevelStageEvent event) {
        // Only render during the AFTER_TRIPWIRE stage
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS || highlightDuration < tick) { return; }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) { return; }

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(BasicLineRenderType.BASIC_LINES);

        PoseStack stack = event.getPoseStack();
        stack.pushPose();

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.translate(-cam.x, -cam.y, -cam.z);

        int color, alpha;
        switch (highlightType) {
            case ORE_SENSE:
                color = (highlightBlock != null && ORE_DISPLAY_COLORS.containsKey(highlightBlock) ? ORE_DISPLAY_COLORS.get(highlightBlock) : ORE_DISPLAY_COLORS.get(Blocks.AIR));
                alpha = (int) (127.0 * Math.min((highlightDuration - tick) / 20.0, 1.0));
                color = color | (alpha << 24);
                for (BlockPos pos : highlightLocations) {
                    if (mc.level.getBlockState(pos).is(highlightBlock)) {
                        drawCubeOutline(builder, stack, pos, color);
                    }
                }
                break;
            case DEFINED_CONTAINERS:
                color = highlightColor;
                alpha = (int) (127.0 * Math.min((highlightDuration - tick) / 20.0, 1.0));
                color = color | (alpha << 24);
                for (BlockPos pos : highlightLocations) {
                    if (isVaildLootContainer(mc.player, mc.level.getBlockEntity(pos))) {
                        drawCubeOutline(builder, stack, pos, color);
                    }
                }
                break;
        }

        RenderSystem.disableDepthTest();
        buffer.endBatch(BasicLineRenderType.BASIC_LINES);
        stack.popPose();
    }

    private static void drawCubeOutline(VertexConsumer builder, PoseStack stack, BlockPos pos, int color) {
        PoseStack.Pose lastPost = stack.last();
        Matrix4f mat4 = lastPost.pose();
        Matrix3f mat3 = lastPost.normal();

        float f1 = pos.getX();
        float f2 = pos.getY();
        float f3 = pos.getZ();

        builder.vertex(mat4, f1, f2, f3).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1+1, f2, f3).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1, f2+1, f3).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1, f2, f3+1).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1+1, f2, f3+1).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1, f2+1, f3+1).color(color).normal(mat3, 1, 0, 0).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3+1).color(color).normal(mat3, 1, 0, 0).endVertex();


        builder.vertex(mat4, f1, f2, f3).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1, f2, f3+1).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1+1, f2, f3).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1+1, f2, f3+1).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1, f2+1, f3).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1, f2+1, f3+1).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3).color(color).normal(mat3, 0, 0, 1).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3+1).color(color).normal(mat3, 0, 0, 1).endVertex();


        builder.vertex(mat4, f1, f2, f3).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1, f2+1, f3).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1+1, f2, f3).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1, f2, f3+1).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1, f2+1, f3+1).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1+1, f2, f3+1).color(color).normal(mat3,0, 1, 0).endVertex();
        builder.vertex(mat4, f1+1, f2+1, f3+1).color(color).normal(mat3,0, 1, 0).endVertex();
    }
}
