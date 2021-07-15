package com.vulp.tomes;

import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TomesRegistry {

    public static final ItemGroup TOMES_TAB = new ItemGroup("tomes") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.archaic_tome);
        }
    };

    @SubscribeEvent
    public static void itemRegistryEvent(final RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ItemInit.archaic_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("archaic_tome")),
                ItemInit.living_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("living_tome")),
                ItemInit.cursed_tome = new TomeItem(new Item.Properties().group(TOMES_TAB).maxDamage(1000)).setRegistryName(location("cursed_tome"))
        );
        Tomes.LOGGER.info("Items Registered!");
    }

    @SubscribeEvent
    public static void enchantRegistryEvent(final RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(
                EnchantmentInit.self_propulsion.setRegistryName(location("self_propulsion"))
        );

        Tomes.LOGGER.info("Enchantments Registered!");
    }

    public static ResourceLocation location(String name) {
        return new ResourceLocation(Tomes.MODID, name);
    }

}
