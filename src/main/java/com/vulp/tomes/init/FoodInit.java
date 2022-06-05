package com.vulp.tomes.init;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class FoodInit {

    public static final Food archaic_heart = new Food.Builder().hunger(2).saturation(0.2F).meat().setAlwaysEdible().build();
    public static final Food beating_heart = new Food.Builder().hunger(2).saturation(0.2F).effect(() -> new EffectInstance(Effects.ABSORPTION, 1200, 0), 1.0F).meat().setAlwaysEdible().build();
    public static final Food sweet_heart = new Food.Builder().hunger(2).saturation(0.2F).effect(() -> new EffectInstance(EffectInit.antidotal, 1200, 1), 1.0F).meat().setAlwaysEdible().build();

}
