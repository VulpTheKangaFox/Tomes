package com.vulp.tomes.client.renderer;

import com.vulp.tomes.Tomes;
import com.vulp.tomes.items.TomeIndex;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class TomeTextureCache {

    private static final HashMap<TomeIndex, HashMap<Integer, DynamicTexture>> cache = new HashMap<>();

    public DynamicTexture getTexture(int cooldown, TomeIndex tome) {
        return cache.get(tome).get(cooldown);
    }

    public void initTextures() {
        if (cache.isEmpty()) {
            for (TomeIndex tome : TomeIndex.values()) {
                try {
                    NativeImage book = NativeImage.read(Tomes.class.getResourceAsStream("/assets/" + Tomes.MODID + "/textures/item/" + tome.getString() + "_tome.png"));
                    NativeImage overlay = NativeImage.read(Tomes.class.getResourceAsStream("/assets/" + Tomes.MODID + "/textures/item/" + tome.getString() + "_tome_cooldown.png"));

                    for (int i = 0; i < 16; i++) {

                        for (int x = 0; x < book.getWidth(); x++) {
                            for (int y = book.getHeight() - 1; y > ((book.getHeight() / 16) * i) - 1; y--) {
                                if (y > -1) {
                                    if (NativeImage.getAlpha(overlay.getPixelRGBA(x, y)) > 1) {
                                        book.setPixelRGBA(x, y, overlay.getPixelRGBA(x, y));
                                    }
                                }
                            }
                        }
                        if (!cache.containsKey(tome)) {
                            cache.put(tome, new HashMap<>());
                        }
                        cache.get(tome).put(i, new DynamicTexture(book));
                    }
                } catch (Exception e) {
                    System.out.println("Missing files! Both textures need to be present!:");
                    System.out.println("/assets/" + Tomes.MODID + "/textures/item/" + tome.getString() + "_tome.png");
                    System.out.println("/assets/" + Tomes.MODID + "/textures/item/" + tome.getString() + "_tome_cooldown.png");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

}
