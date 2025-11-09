package toomda.playerenhancer.logic;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import toomda.playerenhancer.PlayerEnhancer;

public final class StatApplier {
    private static final Identifier ATTACK_BOOST_MODIFIER_ID =
            Identifier.of(PlayerEnhancer.MOD_ID, "attack_boost");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
                var inv = getInventory(sp);
                if (inv == null) continue;

                var attr = sp.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (attr == null) continue;

                boolean want = !inv.getStack(1).isEmpty() && inv.getStack(1).isOf(Items.NETHER_BRICK);
                var existing = attr.getModifier(ATTACK_BOOST_MODIFIER_ID);

                if (want && existing == null) {
                    attr.addTemporaryModifier(new EntityAttributeModifier(
                            ATTACK_BOOST_MODIFIER_ID,
                            0.20,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    ));
                } else if (!want && existing != null) {
                    attr.removeModifier(ATTACK_BOOST_MODIFIER_ID);
                }
            }
        });
    }

    private static Inventories.TargetInventory getInventory(PlayerEntity target) {
        if (target.getWorld().isClient) return null;
        var sw = (net.minecraft.server.world.ServerWorld) target.getWorld();
        var state = TargetAugmentState.get(sw);
        return state.getOrCreate(target.getUuidAsString());
    }
}
