package com.vulp.tomes.items.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.vulp.tomes.init.RecipeInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GobletOfHeartsRecipe implements IRecipe<IInventory> {

    protected final ResourceLocation id;
    protected final Ingredient[] ingredients;
    protected final ItemStack[] itemResults;
    protected final EffectInstance[] effectResults;
    protected final EntityType<?>[] entityResults;
    protected final Integer[] entityCounts;

    public GobletOfHeartsRecipe(ResourceLocation id, Ingredient[] ingredients, @Nullable ItemStack[] itemResults, @Nullable EffectInstance[] effectResults, @Nullable EntityType<?>[] entityResults, @Nullable Integer[] entityCounts) {
        this.id = id;
        this.ingredients = ingredients;
        this.itemResults = itemResults;
        this.effectResults = effectResults;
        this.entityResults = entityResults;
        this.entityCounts = entityCounts;
    }

    public void trigger(World world, BlockPos pos, PlayerEntity player) {
        final Random rand = new Random();
        if (this.itemResults != null) {
            for (ItemStack itemstack : this.itemResults) {
                if (!itemstack.isEmpty()) {
                    double d0 = EntityType.ITEM.getWidth();
                    double d1 = 1.0D - d0;
                    double d2 = d0 / 2.0D;
                    double d3 = Math.floor(pos.getX()) + rand.nextDouble() * d1 + d2;
                    double d4 = pos.getY() + d1;
                    double d5 = Math.floor(pos.getZ()) + rand.nextDouble() * d1 + d2;
                    ItemEntity itementity = new ItemEntity(world, d3, d4, d5, itemstack.copy().split(rand.nextInt(21) + 10));
                    itementity.setMotion(rand.nextGaussian() * (double) 0.02F, rand.nextGaussian() * (double) 0.05F + (double) 0.2F, rand.nextGaussian() * (double) 0.02F);
                    world.addEntity(itementity);
                }
            }
        }
        if (this.effectResults != null) {
            for (EffectInstance effectResult : this.effectResults) {
                player.addPotionEffect(new EffectInstance(effectResult.getPotion(), effectResult.getDuration(), effectResult.getAmplifier(), false, effectResult.doesShowParticles()));
            }
        }
        if (this.entityResults != null) {
            for (int i = 0; i < this.entityResults.length; i++) {
                int count = 1;
                if (this.entityCounts != null) {
                    count = this.entityCounts[i];
                }
                for (int i1 = 0; i1 < count; i1++) {
                    this.entityResults[i].spawn((ServerWorld) world, null, player, pos.up().add((rand.nextFloat() * 0.2F) - 0.1F, 0.0F, (rand.nextFloat() * 0.2F) - 0.1F), SpawnReason.MOB_SUMMONED, true, false);
                }
            }
        }
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        List<ItemStack> invList = new ArrayList<>(Collections.emptyList());
        int invSuccesses = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                invList.add(inv.getStackInSlot(i));
            }
        }
        main:
        for (Ingredient ingredient : this.ingredients) {
            for (ItemStack stack : invList) {
                if (ingredient.test(stack)) {
                    invSuccesses++;
                    continue main;
                }
            }
            return false;
        }
        return invSuccesses == invList.size();
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.from(Ingredient.EMPTY, this.ingredients);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeInit.goblet_of_hearts_serializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeInit.goblet_crafting;
    }

    public static class GobletOfHeartsRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GobletOfHeartsRecipe> {

        @Override
        public GobletOfHeartsRecipe read(ResourceLocation recipeId, JsonObject json) {
            // Get ingredients:
            JsonArray ingredientArray = JSONUtils.getJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    ingredients.add(ingredient);
                }
            }
            if (ingredients.size() > 8) {
                throw new JsonSyntaxException("Too many ingredients: 8 is the maximum");
            }
            boolean hasResult = false;
            // Get item results:
            JsonArray itemResultArray = getOptionalJsonArray(json, "item_results");
            NonNullList<ItemStack> itemStacks = NonNullList.create();
            if (itemResultArray != null) {
                hasResult = true;
                for (int i = 0; i < itemResultArray.size(); ++i) {
                    ItemStack itemStack = ShapedRecipe.deserializeItem(itemResultArray.get(i).getAsJsonObject());
                    if (!itemStack.isEmpty()) {
                        itemStacks.add(itemStack);
                    }
                }
            }
            // Get effect results:
            JsonArray effectResultArray = getOptionalJsonArray(json, "effect_results");
            NonNullList<EffectInstance> effects = NonNullList.create();
            if (effectResultArray != null) {
                hasResult = true;
                for (int i = 0; i < effectResultArray.size(); ++i) {
                    JsonObject object = effectResultArray.get(i).getAsJsonObject();
                    String s = JSONUtils.getString(object, "effect");
                    Effect effect = Registry.EFFECTS.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + s + "'"));
                    int level = JSONUtils.getInt(object, "level", 1) - 1;
                    int duration = JSONUtils.getInt(object, "duration", 120);
                    boolean particles = JSONUtils.getBoolean(object, "particles", true);
                    effects.add(new EffectInstance(effect, duration, level, false, particles));
                }
            }
            // Get entity results:
            JsonArray entityResultArray = getOptionalJsonArray(json, "entity_results");
            NonNullList<EntityType<?>> entities = NonNullList.create();
            List<Integer> entityCounts = new ArrayList<>(Collections.emptyList());
            if (entityResultArray != null) {
                hasResult = true;
                for(int i = 0; i < entityResultArray.size(); ++i) {
                    JsonObject object = entityResultArray.get(i).getAsJsonObject();
                    String s = JSONUtils.getString(object, "entity");
                    EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + s + "'"));
                    int count = JSONUtils.getInt(object, "count", 1);
                    entities.add(entityType);
                    entityCounts.add(count);
                }
            }
            if (!hasResult) {
                throw new JsonSyntaxException("Recipe must contain a result of some kind");
            }
            return new GobletOfHeartsRecipe(recipeId, ingredients.toArray(new Ingredient[0]), itemStacks.isEmpty() ? null : itemStacks.toArray(new ItemStack[0]), effects.isEmpty() ? null : effects.toArray(new EffectInstance[0]), entities.isEmpty() ? null : entities.toArray(new EntityType<?>[0]), entityCounts.isEmpty() ? null : entityCounts.toArray(new Integer[0]));
        }

        @Nullable
        @Override
        public GobletOfHeartsRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int length = buffer.readInt();
            Ingredient[] ingredients = new Ingredient[length];
            ItemStack[] items = null;
            EffectInstance[] effects = null;
            EntityType<?>[] entities = null;
            Integer[] entityCounts = null;
            for (int i = 0; i < length; i++) {
                ingredients[i] = Ingredient.read(buffer);
            }
            // Item results:
            if (buffer.readBoolean()) {
                int itemLength = buffer.readInt();
                items = new ItemStack[itemLength];
                for (int i = 0; i < itemLength; i++) {
                    items[i] = buffer.readItemStack();
                }
            }
            // Effect results:
            if (buffer.readBoolean()) {
                int effectLength = buffer.readInt();
                effects = new EffectInstance[effectLength];
                for (int i = 0; i < effectLength; i++) {
                    effects[i] = EffectInstance.read(buffer.readCompoundTag());
                }
            }
            // Entity results:
            if (buffer.readBoolean()) {
                int entityLength = buffer.readInt();
                entities = new EntityType<?>[entityLength];
                entityCounts = new Integer[entityLength];
                for (int i = 0; i < entityLength; i++) {
                    String entityTypeKey = buffer.readCompoundTag().getString("key");
                    entities[i] = EntityType.byKey(entityTypeKey).orElseThrow(() -> new IllegalStateException("Entity: " + entityTypeKey + " does not exist"));
                    entityCounts[i] = buffer.readInt();
                }
            }
            return new GobletOfHeartsRecipe(recipeId, ingredients, items, effects, entities, entityCounts);
        }

        @Override
        public void write(PacketBuffer buffer, GobletOfHeartsRecipe recipe) {
            buffer.writeInt(recipe.ingredients.length);
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buffer);
            }
            boolean items = false;
            if (recipe.itemResults != null) {
                items = true;
                buffer.writeInt(recipe.itemResults.length);
                for (ItemStack itemResult : recipe.itemResults) {
                    buffer.writeItemStack(itemResult);
                }
            }
            buffer.writeBoolean(items);
            boolean effects = false;
            if (recipe.effectResults != null) {
                effects = true;
                buffer.writeInt(recipe.effectResults.length);
                for (EffectInstance effectResult : recipe.effectResults) {
                    buffer.writeCompoundTag(effectResult.write(new CompoundNBT()));
                }
            }
            buffer.writeBoolean(effects);
            boolean entities = false;
            if (recipe.entityResults != null) {
                entities = true;
                buffer.writeInt(recipe.entityResults.length);
                for (EntityType<?> entityType : recipe.entityResults) {
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putString("key", EntityType.getKey(entityType).toString());
                    buffer.writeCompoundTag(nbt);
                }
                if (recipe.entityCounts != null) {
                    for (Integer entityCount : recipe.entityCounts) {
                        buffer.writeInt(entityCount);
                    }
                }
            }
            buffer.writeBoolean(entities);
        }

        @Nullable
        public static JsonArray getOptionalJsonArray(JsonObject json, String memberName) {
            if (json.has(memberName)) {
                return JSONUtils.getJsonArray(json.get(memberName), memberName);
            } else {
                return null;
            }
        }

    }

}
