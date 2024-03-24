package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;

/**
 * Serializer for {@link MaterialRecipe}
 */
public class MaterialRecipeSerializer implements LoggingRecipeSerializer<MaterialRecipe> {
  @Override
  public MaterialRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    String group = GsonHelper.getAsString(json, "group", "");
    Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
    int value = GsonHelper.getAsInt(json, "value", 1);
    int needed = GsonHelper.getAsInt(json, "needed", 1);
    MaterialVariantId materialId = MaterialVariantId.fromJson(json, "material");
    ItemOutput leftover = ItemOutput.EMPTY;
    if (value > 1) {
      leftover = ItemOutput.Loadable.OPTIONAL_STACK.getOrEmpty(json, "leftover");
    }
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, materialId, leftover);
  }

  @Nullable
  @Override
  public MaterialRecipe fromNetworkSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    String group = buffer.readUtf(Short.MAX_VALUE);
    Ingredient ingredient = Ingredient.fromNetwork(buffer);
    int value = buffer.readInt();
    int needed = buffer.readInt();
    MaterialVariantId materialId = MaterialVariantId.parse(buffer.readUtf(Short.MAX_VALUE));
    ItemOutput leftover = ItemOutput.read(buffer);
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, materialId, leftover);
  }

  @Override
  public void toNetworkSafe(FriendlyByteBuf buffer, MaterialRecipe recipe) {
    buffer.writeUtf(recipe.group);
    recipe.ingredient.toNetwork(buffer);
    buffer.writeInt(recipe.value);
    buffer.writeInt(recipe.needed);
    buffer.writeUtf(recipe.material.getVariant().toString());
    recipe.leftover.write(buffer);
  }
}
