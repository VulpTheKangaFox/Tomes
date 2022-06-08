package com.vulp.tomes.events;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.mojang.datafixers.util.Pair;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.blocks.tile.GobletOfHeartsTileEntity;
import com.vulp.tomes.client.renderer.RenderTypes;
import com.vulp.tomes.client.renderer.tile.GobletOfHeartsRenderer;
import com.vulp.tomes.config.TomesConfig;
import com.vulp.tomes.effects.StarryFormEffect;
import com.vulp.tomes.enchantments.EnchantClueHolder;
import com.vulp.tomes.init.EnchantmentInit;
import com.vulp.tomes.init.ItemInit;
import com.vulp.tomes.init.TileInit;
import com.vulp.tomes.items.TomeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid= Tomes.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class RenderEvents {

    private static boolean HOOK_TOGGLE = false;
    private static ActiveRenderInfo RENDER_INFO;
    private static float LAST_CAMERA_YAW;
    private static float LAST_CAMERA_PITCH;
    private static float LAST_CAMERA_ROLL;
    private static final Random rand = new Random();

    @SubscribeEvent
    public static void onRenderHandEvent(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof TomeItem && stack.isEnchanted()) {
            Minecraft minecraft = Minecraft.getInstance();
            TomeItem tome = (TomeItem) stack.getItem();
            ItemStack newStack = stack.copy();
            if (tome == ItemInit.archaic_tome) {
                newStack = new ItemStack(ItemInit.archaic_tome_open);
            } else if (tome == ItemInit.living_tome) {
                newStack = new ItemStack(ItemInit.living_tome_open);
            } else if (tome == ItemInit.cursed_tome) {
                newStack = new ItemStack(ItemInit.cursed_tome_open);
            }
            minecraft.getFirstPersonRenderer().renderItemInFirstPerson(minecraft.player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), newStack, event.getEquipProgress(), event.getMatrixStack(), event.getBuffers(), event.getLight());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        GobletOfHeartsRenderer.tick();
        Set<LivingEntity> starryFormTracker = StarryFormEffect.getTracker();
        if (starryFormTracker != null && starryFormTracker.size() > 0) {
            World world = Minecraft.getInstance().world;
            int r = rand.nextInt(5);
            float[] color;
            if (r == 0) {
                color = new float[]{0.102F, 0.133F, 0.231F};
            } else if (r == 1) {
                color = new float[]{0.106F, 0.216F, 0.196F};
            } else if (r == 2) {
                color = new float[]{0.173F, 0.149F, 0.231F};
            } else {
                color = new float[]{0.004F, 0.031F, 0.066F};
            }
            // TODO: THIS KEEPS RETURNING NULLPOINTEREXCEPTION!
            for (LivingEntity entity : starryFormTracker) {
                if (entity != null && world != null) {
                    for (int i = 0; i < 2; ++i) {
                        // TODO: Custom particle.
                        world.addParticle(new RedstoneParticleData(color[0], color[1], color[2], 1.0F), entity.getPosXRandom(1.0D), entity.getPosYRandom(), entity.getPosZRandom(1.0D), 1.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltipEvent(RenderTooltipEvent.Pre event) {
        // Latch helps make sure that calling func_243308_b below doesn't cause an infinite loop of forge hook events.
        if (HOOK_TOGGLE) {
            HOOK_TOGGLE = false;
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if (player != null) {
            PlayerInventory inventory = player.inventory;
            boolean hasTome = inventory != null && Arrays.stream(new ItemStack[]{inventory.getCurrentItem(), inventory.offHandInventory.get(0)}).anyMatch(item -> item.getItem() instanceof TomeItem && EnchantmentHelper.getEnchantments(item).containsKey(EnchantmentInit.linguist));
            if (TomesConfig.linguist_enabled.get() && hasTome) {
                Screen screen = minecraft.currentScreen;
                if (screen instanceof ContainerScreen && ((ContainerScreen<?>) screen).getContainer() instanceof EnchantmentContainer) {
                    ContainerScreen<?> containerScreen = (ContainerScreen<?>) screen;
                    EnchantmentContainer container = (EnchantmentContainer) containerScreen.getContainer();
                    EnchantClueHolder clueHolder = EnchantClueHolder.decodeClues(container.enchantClue);
                    for (int j = 0; j < 3; ++j) {
                        List<Pair<Enchantment, Integer>> clueList = clueHolder.getData(j);
                        List<ITextComponent> list = Lists.newArrayList();
                        int k = 0;
                        int i1 = j + 1;
                        ItemStack stack = container.tableInventory.getStackInSlot(0);
                        if (isPointInRegion(60, 14 + 19 * j, 108, 17, event.getX(), event.getY(), containerScreen.getGuiLeft(), containerScreen.getGuiTop()) && clueList.size() > 0 && !stack.isEmpty() && !stack.isEnchanted() && stack.isEnchantable()) {
                            for (Pair<Enchantment, Integer> clue : clueList) {
                                if (clue.getFirst() != null) {
                                    list.add(getDisplayName(clue.getFirst(), clue.getSecond()));
                                } else {
                                    list.add(new StringTextComponent(". . .").mergeStyle(TextFormatting.AQUA));
                                }
                            }
                            if (clueList.isEmpty()) {
                                list.add(new StringTextComponent(""));
                                list.add(new TranslationTextComponent("forge.container.enchant.limitedEnchantability").mergeStyle(TextFormatting.RED));
                            } else if (!player.abilities.isCreativeMode) {
                                list.add(StringTextComponent.EMPTY);
                                if (player.experienceLevel < k) {
                                    list.add((new TranslationTextComponent("container.enchant.level.requirement", (container).enchantLevels[j])).mergeStyle(TextFormatting.RED));
                                } else {
                                    IFormattableTextComponent iformattabletextcomponent;
                                    if (i1 == 1) {
                                        iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.one");
                                    } else {
                                        iformattabletextcomponent = new TranslationTextComponent("container.enchant.lapis.many", i1);
                                    }

                                    list.add(iformattabletextcomponent.mergeStyle(container.getLapisAmount() >= i1 ? TextFormatting.GRAY : TextFormatting.RED));
                                    IFormattableTextComponent iformattabletextcomponent1;
                                    if (i1 == 1) {
                                        iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.one");
                                    } else {
                                        iformattabletextcomponent1 = new TranslationTextComponent("container.enchant.level.many", i1);
                                    }

                                    list.add(iformattabletextcomponent1.mergeStyle(TextFormatting.GRAY));
                                }
                            }
                            HOOK_TOGGLE = true;
                            containerScreen.func_243308_b(event.getMatrixStack(), list, event.getX(), event.getY());
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

    }

    private static boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY, int guiLeft, int guiTop) {
        mouseX = mouseX - (double)guiLeft;
        mouseY = mouseY - (double)guiTop;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    private static ITextComponent getDisplayName(Enchantment enchantment, int level) {
        IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(enchantment.getName());
        if (enchantment.isCurse()) {
            iformattabletextcomponent.mergeStyle(TextFormatting.RED);
        } else {
            iformattabletextcomponent.mergeStyle(TextFormatting.AQUA);
        }

        if (level != 1 || enchantment.getMaxLevel() != 1) {
            iformattabletextcomponent.appendString(" ").appendSibling(new TranslationTextComponent("enchantment.level." + level));
        }

        return iformattabletextcomponent;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Minecraft.isFabulousGraphicsEnabled()) {
            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.push();
            WorldRenderer worldRenderer = event.getContext();
            RENDER_INFO.setAnglesInternal(LAST_CAMERA_YAW, LAST_CAMERA_PITCH);
            Vector3d vector3d = RENDER_INFO.getProjectedView();
            double d0 = vector3d.getX();
            double d1 = vector3d.getY();
            double d2 = vector3d.getZ();
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            //gameRenderer.resetProjectionMatrix(matrix4f);
            ClippingHelper clippinghelper;
            if (worldRenderer.debugFixedClippingHelper != null) {
                clippinghelper = worldRenderer.debugFixedClippingHelper;
                clippinghelper.setCameraPosition(worldRenderer.debugTerrainFrustumPosition.x, worldRenderer.debugTerrainFrustumPosition.y, worldRenderer.debugTerrainFrustumPosition.z);
            } else {
                clippinghelper = new ClippingHelper(matrix4f, event.getProjectionMatrix());
                clippinghelper.setCameraPosition(d0, d1, d2);
            }
            RenderSystem.pushMatrix();
            RenderSystem.depthMask(false);
            IRenderTypeBuffer.Impl irendertypebuffer1 = Minecraft.getInstance().worldRenderer.renderTypeTextures.getBufferSource();
            for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : worldRenderer.renderInfos) {
                List<TileEntity> list = worldrenderer$localrenderinformationcontainer.renderChunk.getCompiledChunk().getTileEntities();
                if (!list.isEmpty()) {
                    for (TileEntity tileEntity : list) {
                        if (!(tileEntity instanceof GobletOfHeartsTileEntity) || !clippinghelper.isBoundingBoxInFrustum(tileEntity.getRenderBoundingBox())) {
                            continue;
                        }
                        if (((GobletOfHeartsTileEntity) tileEntity).canCraft()) {
                            BlockPos blockPos = tileEntity.getPos();
                            matrixStack.push();
                            matrixStack.translate((double) blockPos.getX() - d0, (double) blockPos.getY() - d1, (double) blockPos.getZ() - d2);
                            GobletOfHeartsRenderer.renderReadyGlow(event.getMatrixStack(), irendertypebuffer1.getBuffer(RenderTypes.getLightFlare()));
                            matrixStack.pop();
                        }
                    }
                }
            }
            matrixStack.pop();
            RenderSystem.popMatrix();
            irendertypebuffer1.finish();
            RenderSystem.depthMask(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        RENDER_INFO = event.getInfo();
        LAST_CAMERA_YAW = event.getYaw();
        LAST_CAMERA_PITCH = event.getPitch();
        LAST_CAMERA_ROLL = event.getRoll();
    }

}
