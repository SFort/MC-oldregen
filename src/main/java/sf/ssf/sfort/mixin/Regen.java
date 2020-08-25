package sf.ssf.sfort.mixin;


import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
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

	/*
	i have put way to much time and failed projects into trying to figure out redirects
	and at this point i think I'm going to start pretending they don't exist

	@Redirect(method = "Lnet/minecraft/entity/player/HungerManager;update(Lnet/minecraft/entity/player/PlayerEntity;)V",
			at= @At(target = "Lnet/minecraft/entity/player/HungerManager;foodSaturationLevel:Ljava/lang/Float",
					value = "FIELD",opcode = Opcodes.GETFIELD, ordinal = 3))
	private float getSaturation(HungerManager i) {
		return 0.0F;
	}
	*/
	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	public void update(PlayerEntity player, CallbackInfo info) {
		if (sf.ssf.sfort.Regen.ignoreGamerules||(player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION))
				&& this.foodSaturationLevel > 0.0F && player.canFoodHeal()) {
			++this.foodStarvationTimer;
			if (this.foodStarvationTimer >= sf.ssf.sfort.Regen.delay) {
				player.heal(sf.ssf.sfort.Regen.heal);
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - sf.ssf.sfort.Regen.cost, 0.0F);
				this.foodStarvationTimer = 0;
			}
			info.cancel();
		}
	}
}
