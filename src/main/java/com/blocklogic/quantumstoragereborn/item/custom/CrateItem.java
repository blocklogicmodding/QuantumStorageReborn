package com.blocklogic.quantumstoragereborn.item.custom;

import com.blocklogic.quantumstoragereborn.component.QSRDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CrateItem extends BlockItem {
    public CrateItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        QSRDataComponents.InventoryData inventoryData = stack.get(QSRDataComponents.INVENTORY_DATA.get());
        if (inventoryData != null && !inventoryData.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.quantumstoragereborn.crate.contains_items")
                    .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC));
        }
    }
}