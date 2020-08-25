package sf.ssf.sfort.mixin;


import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class Regen {
	@Shadow
	private int foodLevel;
	@Shadow
	private float exhaustion;
	@Shadow
	private int foodStarvationTimer;
	@Shadow
	private float foodSaturationLevel;
	@Shadow
	private int prevFoodLevel;

	//Practically and @Overwrite
	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	public void update(PlayerEntity player,CallbackInfo info) {
		Difficulty difficulty = player.world.getDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}
		boolean bl = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if ((sf.ssf.sfort.Regen.ignoreGamerules || bl) && this.foodSaturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= sf.ssf.sfort.Regen.sat_food_req) {
			++this.foodStarvationTimer;
			if (this.foodStarvationTimer >= sf.ssf.sfort.Regen.sat_delay) {
				player.heal(sf.ssf.sfort.Regen.sat_heal);
				this.addExhaustion(sf.ssf.sfort.Regen.sat_cost);
				this.foodStarvationTimer = 0;
			}
		} else if (bl && this.foodLevel >= sf.ssf.sfort.Regen.food_req && player.canFoodHeal()) {
			++this.foodStarvationTimer;
			if (this.foodStarvationTimer >= sf.ssf.sfort.Regen.food_delay) {
				player.heal(sf.ssf.sfort.Regen.food_heal);
				this.addExhaustion(sf.ssf.sfort.Regen.food_cost);
				this.foodStarvationTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			++this.foodStarvationTimer;
			if (this.foodStarvationTimer >= sf.ssf.sfort.Regen.starve_delay) {
				if (sf.ssf.sfort.Regen.starve_kill){
					player.damage(DamageSource.STARVE, Float.MAX_VALUE);
				}else
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.damage(DamageSource.STARVE, sf.ssf.sfort.Regen.starve_cost);
				}
				this.foodStarvationTimer = 0;
			}
		} else {
			this.foodStarvationTimer = 0;
		}
		info.cancel();
	}
	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
	}
}
