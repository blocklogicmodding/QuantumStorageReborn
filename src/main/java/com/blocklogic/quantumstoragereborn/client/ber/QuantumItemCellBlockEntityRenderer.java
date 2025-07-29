package com.blocklogic.quantumstoragereborn.client.ber;

import com.blocklogic.quantumstoragereborn.entity.custom.QuantumItemCellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class QuantumItemCellBlockEntityRenderer implements BlockEntityRenderer<QuantumItemCellBlockEntity> {
    public QuantumItemCellBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(QuantumItemCellBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        QuantumItemCellBlockEntity.CellContents contents = blockEntity.getContents();

        if (contents.storedItemId().isEmpty() || contents.count() <= 0) {
            return;
        }

        Item item = BuiltInRegistries.ITEM.get(contents.storedItemId().get());
        ItemStack stack = new ItemStack(item);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.65F, 0.5F);
        poseStack.scale(0.6F, 0.6F, 0.6F);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.renderRotation()));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, blockEntity.getLevel(), 1);
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}