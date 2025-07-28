package com.blocklogic.quantumstoragereborn;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import com.blocklogic.quantumstoragereborn.container.QSRMenuTypes;
import com.blocklogic.quantumstoragereborn.container.screen.*;
import com.blocklogic.quantumstoragereborn.entity.QSRBlockEntities;
import com.blocklogic.quantumstoragereborn.entity.custom.*;
import com.blocklogic.quantumstoragereborn.item.QSRCreativeTab;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(QuantumStorageReborn.MODID)
public class QuantumStorageReborn {

    public static final String MODID = "quantumstoragereborn";

    public static final Logger LOGGER = LogUtils.getLogger();

    public QuantumStorageReborn(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        QSRItems.register(modEventBus);
        QSRBlocks.register(modEventBus);
        QSRCreativeTab.register(modEventBus);
        QSRBlockEntities.register(modEventBus);
        QSRMenuTypes.register(modEventBus);

        modEventBus.addListener(CopperCrateBlockEntity::registerCapabilities);
        modEventBus.addListener(IronCrateBlockEntity::registerCapabilities);
        modEventBus.addListener(GoldCrateBlockEntity::registerCapabilities);
        modEventBus.addListener(DiamondCrateBlockEntity::registerCapabilities);
        modEventBus.addListener(NetheriteCrateBlockEntity::registerCapabilities);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(QSRBlocks.QUANTUM_ITEM_CELL.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(QSRBlocks.QUANTUM_FLUID_CELL.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(QSRMenuTypes.COPPER_CRATE_MENU.get(), CopperCrateScreen::new);
            event.register(QSRMenuTypes.IRON_CRATE_MENU.get(), IronCrateScreen::new);
            event.register(QSRMenuTypes.GOLD_CRATE_MENU.get(), GoldCrateScreen::new);
            event.register(QSRMenuTypes.DIAMOND_CRATE_MENU.get(), DiamondCrateScreen::new);
            event.register(QSRMenuTypes.NETHERITE_CRATE_MENU.get(), NetheriteCrateScreen::new);
        }
    }
}
