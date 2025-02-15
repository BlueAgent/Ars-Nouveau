package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class GlyphPressRecipe implements Recipe<Container> {

    private final ResourceLocation id;

    public final SpellTier tier;

    public final ItemStack reagent;

    public final ItemStack output;
    public static final String RECIPE_ID = "glyph_recipe";

    public GlyphPressRecipe(ResourceLocation id, SpellTier tier, ItemStack reagent, ItemStack output){
        this.id = id;
        this.tier = tier;
        this.reagent = reagent;
        this.output = output;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Container inv) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.PRESS_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(new ResourceLocation(ArsNouveau.MODID, "glyph_recipe"));
    }

    public ItemStack getClay(){
        return getClayFromTier(tier);
    }

    public static ItemStack getClayFromTier(SpellTier tier){
        if (SpellTier.ONE.equals(tier)) {
            return new ItemStack(ItemsRegistry.MAGIC_CLAY);
        } else if (SpellTier.TWO.equals(tier)) {
            return new ItemStack(ItemsRegistry.MARVELOUS_CLAY);
        }
        return new ItemStack(ItemsRegistry.MYTHICAL_CLAY);
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<GlyphPressRecipe> {

        @Override
        public GlyphPressRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            SpellTier tier = fromString(GsonHelper.getAsString(json, "tier", "ONE"));
            ItemStack input = new ItemStack(GsonHelper.getAsItem(json, "input"));
            ItemStack output = new ItemStack(GsonHelper.getAsItem(json, "output"));
            return new GlyphPressRecipe(recipeId, tier, input, output);
        }

        @Nullable
        @Override
        public GlyphPressRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            SpellTier tier = fromString(buffer.readUtf());
            ItemStack input = buffer.readItem();
            ItemStack output = buffer.readItem();
            return new GlyphPressRecipe(recipeId, tier, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GlyphPressRecipe recipe) {
            buf.writeUtf(fromTier(recipe.tier));
            buf.writeItem(recipe.reagent);
            buf.writeItem(recipe.output);
        }

        public String fromTier(SpellTier tier){
            if (SpellTier.ONE.equals(tier)) {
                return "ONE";
            }else if(SpellTier.TWO.equals(tier)){
                return "TWO";
            }else if(SpellTier.THREE.equals(tier)){
                return "THREE";
            }
            return "ONE";
        }

        public SpellTier fromString(String s){
            if(s == null)
                return SpellTier.ONE;
            if(s.equals("ONE"))
                return SpellTier.ONE;
            if(s.equals("TWO")){
                return SpellTier.TWO;
            }
            if(s.equals("THREE")){
                return SpellTier.THREE;
            }
            return SpellTier.ONE;
        }
    }
}
