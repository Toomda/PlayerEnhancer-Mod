package toomda.playerenhancer.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toomda.playerenhancer.logic.Inventories;
import toomda.playerenhancer.logic.TargetAugmentState;

@Mixin(LivingEntity.class)
public class LivingEntityDamageMixin {
	@ModifyArg(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"
			),
			index = 1
	)
	private float playerenhancer$scaleIncomingDamage(float amount) {
		LivingEntity self = (LivingEntity)(Object)this;

		if (!(self instanceof PlayerEntity p) || p.getWorld().isClient) {
			return amount;
		}

		var sw = (net.minecraft.server.world.ServerWorld) p.getWorld();
		var state = TargetAugmentState.get(sw);
		Inventories.TargetInventory inv = state.getOrCreate(p.getUuidAsString());
		if (!inv.getStack(0).isEmpty() && inv.getStack(0).isOf(Items.ARMADILLO_SCUTE)) {
			return amount * 0.6f;
		}
		return amount;
	}
}