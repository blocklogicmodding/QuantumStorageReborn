package com.blocklogic.quantumstoragereborn.container;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.container.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class QSRMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, QuantumStorageReborn.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CopperCrateMenu>> COPPER_CRATE_MENU = registerMenuType("copper_crate_menu", CopperCrateMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<IronCrateMenu>> IRON_CRATE_MENU = registerMenuType("iron_crate_menu", IronCrateMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<GoldCrateMenu>> GOLD_CRATE_MENU = registerMenuType("gold_crate_menu", GoldCrateMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DiamondCrateMenu>> DIAMOND_CRATE_MENU = registerMenuType("diamond_crate_menu", DiamondCrateMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<NetheriteCrateMenu>> NETHERITE_CRATE_MENU = registerMenuType("netherite_crate_menu", NetheriteCrateMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<QuantumItemCellMenu>> QUANTUM_ITEM_CELL_MENU = registerMenuType("quantum_item_cell_menu", QuantumItemCellMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}