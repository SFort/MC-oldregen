package tf.ssf.sfort.oldregen.mixin;


import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tf.ssf.sfort.oldregen.OlRegenConfig;

@Mixin(HungerManager.class)
public class RegenMixin {
	@Shadow
	private int foodLevel;
	@Shadow
	private float exhaustion;
	@Shadow
	private int foodTickTimer;
	@Shadow
	private float saturationLevel;
	@Shadow
	private int prevFoodLevel;

	//Practically and @Overwrite
	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	public void update(PlayerEntity player,CallbackInfo info) {
		Difficulty difficulty = player.getWorld().getDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.saturationLevel > 0.0F) {
				this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}
		boolean bl = player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if ((OlRegenConfig.ignoreGamerules || bl) && this.saturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= OlRegenConfig.sat_food_req) {
			++this.foodTickTimer;
			if (this.foodTickTimer >= OlRegenConfig.sat_delay) {
				player.heal(OlRegenConfig.sat_heal);
				this.addExhaustion(OlRegenConfig.sat_cost);
				this.foodTickTimer = 0;
			}
		} else if (bl && this.foodLevel >= OlRegenConfig.food_req && player.canFoodHeal()) {
			++this.foodTickTimer;
			if (this.foodTickTimer >= OlRegenConfig.food_delay) {
				player.heal(OlRegenConfig.food_heal);
				this.addExhaustion(OlRegenConfig.food_cost);
				this.foodTickTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			++this.foodTickTimer;
			if (this.foodTickTimer >= OlRegenConfig.starve_delay) {
				if (OlRegenConfig.starve_kill){
					player.damage(player.getDamageSources().starve(), Float.MAX_VALUE);
				}else
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.damage(player.getDamageSources().starve(), OlRegenConfig.starve_cost);
				}
				this.foodTickTimer = 0;
			}
		} else {
			this.foodTickTimer = 0;
		}
		info.cancel();
	}
	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
	}
}
