package com.vulp.tomes.plugin.jei.categories;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vulp.tomes.Tomes;
import com.vulp.tomes.TomesRegistry;
import com.vulp.tomes.init.BlockInit;
import com.vulp.tomes.items.crafting.GobletOfHeartsRecipe;
import com.vulp.tomes.plugin.jei.TomesJEI;
import com.vulp.tomes.plugin.jei.wrappers.GobletOfHeartsWrapper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.Arrays;
import java.util.List;

// TODO: This is a mess. Worst case scenario: supply an icon and a ritual name or something. Allow room for a description too.
// TODO: Also you have to make the new ingredient types actually registered.
public class GobletOfHeartsCategory implements IRecipeCategory<GobletOfHeartsWrapper> {

    private static final ResourceLocation UID = TomesRegistry.location("goblet_crafting");
    private static final List<Integer> INPUT_SLOTS = Arrays.asList(0, 1, 2, 3, 4);
    private final String localizedName;
    private final IDrawable icon;
    private final IDrawable background;

    public GobletOfHeartsCategory(IGuiHelper guiHelper) {
        this.localizedName = BlockInit.goblet_of_hearts.getTranslationKey();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlockInit.goblet_of_hearts));
        this.background = guiHelper.drawableBuilder(TomesJEI.RECIPE_GUI_TOMES, 0, 0, 155, 72)
                .addPadding(0, 0, 0, 0)
                .build();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<GobletOfHeartsWrapper> getRecipeClass() {
        return GobletOfHeartsWrapper.class;
    }

    @Override
    public String getTitle() {
        return I18n.format(this.localizedName);
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(GobletOfHeartsWrapper wrapper, IIngredients ingredients) {
        ingredients.setInputIngredients(wrapper.getRecipe().getIngredients());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GobletOfHeartsWrapper recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        // Input:
        guiItemStacks.init(INPUT_SLOTS.get(0), true, 0, 27);
        guiItemStacks.init(INPUT_SLOTS.get(1), true, 5, 5);
        guiItemStacks.init(INPUT_SLOTS.get(2), true, 5, 49);
        guiItemStacks.init(INPUT_SLOTS.get(3), true, 27, 0);
        guiItemStacks.init(INPUT_SLOTS.get(4), true, 27, 54);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(GobletOfHeartsWrapper wrapper, MatrixStack matrixStack, double mouseX, double mouseY) {
        this.drawGoblet(matrixStack);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        float size = 0.75F;
        List<ITextProperties> textLines = wrapper.getDescription() != null ? fontRenderer.getCharacterManager().func_238362_b_(wrapper.getDescription(), (int) ((102) / size), Style.EMPTY) : null;
        if (wrapper.getTitle() != null) {
            fontRenderer.drawTextWithShadow(matrixStack, wrapper.getTitle(), 52/* + (53 - (fontRenderer.getStringPropertyWidth(name) / 2.0F))*/, 4, wrapper.getColor());
        }
        matrixStack.push();
        matrixStack.scale(0.75F, 0.75F, 1.0F);
        int yMod = 0;
        if (textLines != null) {
            for (ITextProperties descriptionLine : textLines) {
                fontRenderer.func_238422_b_(matrixStack, LanguageMap.getInstance().func_241870_a(descriptionLine), 52 / size, (18 / size) + yMod, java.awt.Color.DARK_GRAY.getRGB());
                yMod += (((fontRenderer.FONT_HEIGHT / 2.0F) / size) + 2);
            }
        }
        matrixStack.pop();
    }

    private void drawGoblet(MatrixStack matrixStack) {
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderSystem.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ItemStack goblet = new ItemStack(BlockInit.goblet_of_hearts);
        try {
            RenderSystem.pushMatrix();
            itemRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            itemRenderer.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.translatef(28.0F, 28.0F, 100.0F + itemRenderer.zLevel + 50.0F);
            RenderSystem.translatef(8.0F, 8.0F, 0.0F);
            RenderSystem.scalef(2.0F, -2.0F, 2.0F);
            RenderSystem.scalef(16.0F, 16.0F, 16.0F);
            MatrixStack matrixstack = new MatrixStack();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            IBakedModel bakedmodel = itemRenderer.getItemModelWithOverrides(goblet, null, null);
            boolean flag = !bakedmodel.isSideLit();
            if (flag) {
                RenderHelper.setupGuiFlatDiffuseLighting();
            }

            itemRenderer.renderItem(goblet, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            irendertypebuffer$impl.finish();
            RenderSystem.enableDepthTest();
            if (flag) {
                RenderHelper.setupGui3DDiffuseLighting();
            }

            RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
            RenderSystem.popMatrix();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> String.valueOf(goblet.getItem()));
            crashreportcategory.addDetail("Registry Name", () -> String.valueOf(goblet.getItem().getRegistryName()));
            crashreportcategory.addDetail("Item Damage", () -> String.valueOf(goblet.getDamage()));
            crashreportcategory.addDetail("Item NBT", () -> String.valueOf(goblet.getTag()));
            crashreportcategory.addDetail("Item Foil", () -> String.valueOf(goblet.hasEffect()));
            throw new ReportedException(crashreport);
        }
        RenderSystem.disableBlend();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }


/*    private static final ResourceLocation UID = TomesRegistry.location("goblet_crafting");
    private static final List<Integer> INPUT_SLOTS = Arrays.asList(0, 1, 2, 3, 4);
    private final String localizedName;
    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawable[] scrollbar;
    private double scrollValue = 0;
    public static boolean MOUSE_HELD = false;
    private int buttonPressed = 0;
    private OutputCategoryButton[] buttons;
    private int activeCategory = 0;
    private static int X_MOD;
    private static int Y_MOD;
    // Item outputs, effect outputs, entity types (plus outputs) all need added.
    // Scroll bar, buttons, etc.

    public GobletOfHeartsCategory(IGuiHelper guiHelper) {
        this.localizedName = BlockInit.goblet_of_hearts.getTranslationKey();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BlockInit.goblet_of_hearts));
        this.scrollbar = new IDrawable[]{guiHelper.drawableBuilder(TomesJEI.RECIPE_GUI_TOMES, 72, 92, 12, 15).build(),
                guiHelper.drawableBuilder(TomesJEI.RECIPE_GUI_TOMES, 84, 92, 12, 15).build()};
        this.background = guiHelper.drawableBuilder(TomesJEI.RECIPE_GUI_TOMES, 0, 0, 155, 72)
                .addPadding(0, 0, 0, 0)
                .build();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<GobletOfHeartsRecipe> getRecipeClass() {
        return GobletOfHeartsRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format(this.localizedName);
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(GobletOfHeartsRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        if (recipe.getItemResults() != null) {
            ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemResults()));
        }
        *//*if (recipe.getEffectResults() != null) {
            ingredients.setOutputs(TomesIngredientTypes.EFFECT, Arrays.asList(recipe.getEffectResults()));
        }*//*
        if (recipe.getEntityResults() != null) {
            List<EntityStackHandler> list = new ArrayList<>(Collections.emptyList());
            for (int i = 0; i < recipe.getEntityResults().length; i++) {
                list.add(new EntityStackHandler(recipe.getEntityResults()[i], recipe.getEntityCounts()[i]));
            }
            ingredients.setOutputs(TomesIngredientTypes.ENTITY_STACK, list);
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GobletOfHeartsRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        // Input:
        guiItemStacks.init(INPUT_SLOTS.get(0), true, 0, 27);
        guiItemStacks.init(INPUT_SLOTS.get(1), true, 5, 5);
        guiItemStacks.init(INPUT_SLOTS.get(2), true, 5, 49);
        guiItemStacks.init(INPUT_SLOTS.get(3), true, 27, 0);
        guiItemStacks.init(INPUT_SLOTS.get(4), true, 27, 54);

        // Output:
        int ticker = 5;

        int xCap = 4 - this.buttonPressed;
        int yCap = 0; // Do I need this?

        ItemStack[] itemResults = recipe.getItemResults();

        if (itemResults != null) {
            for (int i = 0; i < itemResults.length; i++) {
                guiItemStacks.init(ticker, false, 49 + (18 * (i % xCap)), (int) (-(this.scrollValue / 56.0D) * (((Math.ceil((float) i / (float) xCap) * 18) - 72))));
                ticker++;
            }
        }



        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(GobletOfHeartsRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Screen screen = getGUI();
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();
        X_MOD = (int) matrix4f.m03; // I really shouldn't be accessing these but still.
        Y_MOD = (int) matrix4f.m13; //
        if (this.buttons == null || !screen.buttons.contains(this.buttons[0])) {
            this.buttons = new OutputCategoryButton[]{new OutputCategoryButton(X_MOD + 135, Y_MOD, 0, this), new OutputCategoryButton(X_MOD + 135, Y_MOD + 26, 1, this), new OutputCategoryButton(X_MOD + 135, Y_MOD + 52, 2, this)};
            for (OutputCategoryButton button : this.buttons) {
                screen.addButton(button);
            }
        } else {
            for (OutputCategoryButton button : this.buttons) {
                button.render(matrixStack, (int) mouseX, (int) mouseY, 0);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderSystem.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ItemStack goblet = new ItemStack(BlockInit.goblet_of_hearts);
        try {
            RenderSystem.pushMatrix();
            itemRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            itemRenderer.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableAlphaTest();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.translatef(28.0F, 28.0F, 100.0F + itemRenderer.zLevel + 50.0F);
            RenderSystem.translatef(8.0F, 8.0F, 0.0F);
            RenderSystem.scalef(2.0F, -2.0F, 2.0F);
            RenderSystem.scalef(16.0F, 16.0F, 16.0F);
            MatrixStack matrixstack = new MatrixStack();
            IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            IBakedModel bakedmodel = itemRenderer.getItemModelWithOverrides(goblet, null, null);
            boolean flag = !bakedmodel.isSideLit();
            if (flag) {
                RenderHelper.setupGuiFlatDiffuseLighting();
            }

            itemRenderer.renderItem(goblet, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
            irendertypebuffer$impl.finish();
            RenderSystem.enableDepthTest();
            if (flag) {
                RenderHelper.setupGui3DDiffuseLighting();
            }

            RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
            RenderSystem.popMatrix();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> String.valueOf(goblet.getItem()));
            crashreportcategory.addDetail("Registry Name", () -> String.valueOf(goblet.getItem().getRegistryName()));
            crashreportcategory.addDetail("Item Damage", () -> String.valueOf(goblet.getDamage()));
            crashreportcategory.addDetail("Item NBT", () -> String.valueOf(goblet.getTag()));
            crashreportcategory.addDetail("Item Foil", () -> String.valueOf(goblet.hasEffect()));
            throw new ReportedException(crashreport);
        }
        RenderSystem.disableBlend();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();

        if (MOUSE_HELD) {
            this.setScrollValue((int) MathHelper.clamp(mouseY - 7, 1, 56));
        }

        this.getScrollBar().draw(matrixStack, 122, (int) this.getScrollValue());
        for(Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrixStack, (int) mouseX, (int) mouseY);
                break;
            }
        }
    }

    private boolean isScrollable(Object outputs) {
        return false;
    }

    private IDrawable getScrollBar() {
        return isScrollable(new Object()) ? this.scrollbar[0] : this.scrollbar[1];
    }

    // TODO: Reset to 0 if the bar is locked (less items/entities/effects than would fit in screen)
    public double getScrollValue() {
        return this.scrollValue;
    }

    public void setScrollValue(double scrollValue) {
        this.scrollValue = scrollValue;
    }

    @Override
    public List<ITextComponent> getTooltipStrings(GobletOfHeartsRecipe recipe, double mouseX, double mouseY) {
        return IRecipeCategory.super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    private static Screen getGUI() {
        return TomesJEI.GUI;
    }

    // TODO: Gotta handle mouse stuff better somehow. Scrolling would be nice to track too.
    @Override
    public boolean handleClick(GobletOfHeartsRecipe recipe, double mouseX, double mouseY, int mouseButton) {
        if (mouseX > 122 && mouseY > 1 && mouseX < 135 && mouseY < 72 && mouseButton == 0) {
            MOUSE_HELD = true;
            if (Minecraft.getInstance().mouseHelper.isLeftDown()) {
                return true;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    protected static class OutputCategoryButton extends AbstractButton {
        private int number;
        private final GobletOfHeartsCategory recipeCategory;
        private static final ITextComponent[] LABELS = setupTooltips();
        private static final ItemStack[] ICONS = new ItemStack[]{new ItemStack(Items.IRON_INGOT), new ItemStack(Items.POTION), new ItemStack(Items.SPIDER_SPAWN_EGG)};

        protected OutputCategoryButton(int x, int y, int number, GobletOfHeartsCategory recipeCategory) {
            super(x, y, 20, 20, StringTextComponent.EMPTY);
            this.number = number;
            this.recipeCategory = recipeCategory;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                int xTotal = this.x - X_MOD;
                int yTotal = this.y - Y_MOD;
                this.isHovered = mouseX >= xTotal && mouseY >= yTotal && mouseX < xTotal + this.width && mouseY < yTotal + this.height;
                if (this.wasHovered != this.isHovered()) {
                    if (this.isHovered()) {
                        if (this.isFocused()) {
                            this.queueNarration(200);
                        } else {
                            this.queueNarration(750);
                        }
                    } else {
                        this.nextNarration = Long.MAX_VALUE;
                    }
                }

                if (this.visible) {
                    this.renderWidget(matrixStack, mouseX, mouseY, partialTicks);
                }

                this.narrate();
                this.wasHovered = this.isHovered();
            }
        }

        public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            Minecraft.getInstance().getTextureManager().bindTexture(TomesJEI.RECIPE_GUI_TOMES);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = 72;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.isSelected()) {
                j += this.width;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            this.blit(matrixStack, this.x - X_MOD, this.y - Y_MOD, j, 72, this.width, this.height);
            Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(ICONS[this.number], this.x + 2, this.y + 2);
            MatrixStack matrixstack = new MatrixStack();
            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                matrixstack.translate(0.0D, 0.0D, (double)(this.zLevel + 200.0F));
                IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                fr.renderString(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215, true, matrixstack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
                irendertypebuffer$impl.finish();
            }
        }

        private static ITextComponent[] setupTooltips() {
            final String string = "gui." + Tomes.MODID + ".jei.";
            return new ITextComponent[]{new TranslationTextComponent(string + "item_button"), new TranslationTextComponent(string + "effect_button"), new TranslationTextComponent(string + "entity_button")};
        }

        private void renderItemstack(MatrixStack matrixStack) {
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
            RenderSystem.enableDepthTest();
            RenderHelper.enableStandardItemLighting();
            Minecraft minecraft = Minecraft.getInstance();
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            ItemStack goblet = new ItemStack(BlockInit.goblet_of_hearts);
            try {
                RenderSystem.pushMatrix();
                itemRenderer.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                itemRenderer.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
                RenderSystem.enableRescaleNormal();
                RenderSystem.enableAlphaTest();
                RenderSystem.defaultAlphaFunc();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.translatef(28.0F, 28.0F, 100.0F + itemRenderer.zLevel + 50.0F);
                RenderSystem.translatef(8.0F, 8.0F, 0.0F);
                RenderSystem.scalef(2.0F, -2.0F, 2.0F);
                RenderSystem.scalef(16.0F, 16.0F, 16.0F);
                MatrixStack matrixstack = new MatrixStack();
                IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                IBakedModel bakedmodel = itemRenderer.getItemModelWithOverrides(goblet, null, null);
                boolean flag = !bakedmodel.isSideLit();
                if (flag) {
                    RenderHelper.setupGuiFlatDiffuseLighting();
                }

                itemRenderer.renderItem(goblet, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
                irendertypebuffer$impl.finish();
                RenderSystem.enableDepthTest();
                if (flag) {
                    RenderHelper.setupGui3DDiffuseLighting();
                }

                RenderSystem.disableAlphaTest();
                RenderSystem.disableRescaleNormal();
                RenderSystem.popMatrix();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                crashreportcategory.addDetail("Item Type", () -> String.valueOf(goblet.getItem()));
                crashreportcategory.addDetail("Registry Name", () -> String.valueOf(goblet.getItem().getRegistryName()));
                crashreportcategory.addDetail("Item Damage", () -> String.valueOf(goblet.getDamage()));
                crashreportcategory.addDetail("Item NBT", () -> String.valueOf(goblet.getTag()));
                crashreportcategory.addDetail("Item Foil", () -> String.valueOf(goblet.hasEffect()));
                throw new ReportedException(crashreport);
            }
            RenderSystem.disableBlend();
            RenderHelper.disableStandardItemLighting();
            RenderSystem.popMatrix();
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
            getGUI().renderTooltip(matrixStack, LABELS[this.number], mouseX, mouseY);
        }

        @Override
        public void onPress() {
            if (this.active) {
                this.recipeCategory.activeCategory = this.number;
            }
        }

        public boolean isSelected() {
            return this.recipeCategory.activeCategory == this.number;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getNumber() {
            return this.number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public GobletOfHeartsCategory getRecipeCategory() {
            return this.recipeCategory;
        }
    }*/

}
