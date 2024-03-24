package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.utils.JsonUtils;

/** Effect to restore hunger to the target */
public record RestoreHungerSpillingEffect(int hunger, float saturation, ItemOutput representative) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("restore_hunger");

  public RestoreHungerSpillingEffect(int hunger, float saturation) {
    this(hunger, saturation, null);
  }

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target instanceof Player player) {
      if (player.canEat(false)) {
        int finalHunger = (int)(hunger * scale);
        player.getFoodData().eat(finalHunger, saturation);
        if (representative != null) {
          ModifierUtil.foodConsumer.onConsume(player, representative.get(), finalHunger, saturation);
        }
      }
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("hunger", hunger);
    json.addProperty("saturation", saturation);
    if (representative != ItemOutput.EMPTY) {
      json.add("representative_item", representative.serialize(false));
    }
    return json;
  }

  public static final JsonDeserializer<RestoreHungerSpillingEffect> LOADER = (element, type, context) -> {
    JsonObject json = element.getAsJsonObject();
    int hunger = GsonHelper.getAsInt(json, "hunger");
    float saturation = GsonHelper.getAsFloat(json, "saturation");
    ItemOutput representative = ItemOutput.Loadable.OPTIONAL_ITEM.getOrEmpty(json, "representative_item");
    return new RestoreHungerSpillingEffect(hunger, saturation, representative);
  };
}
