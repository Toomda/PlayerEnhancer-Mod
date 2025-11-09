package toomda.playerenhancer.logic;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import toomda.playerenhancer.screen.AugmentScreenHandler;

import java.util.UUID;

public final class RightClickHandler {
    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.PASS;
            if (!(entity instanceof PlayerEntity target)) return ActionResult.PASS;

            var state = TargetAugmentState.get(sp.getServerWorld());
            var inv = state.getOrCreate(target.getUuidAsString());
            UUID targetUuid = target.getUuid();

            sp.openHandledScreen(new ExtendedScreenHandlerFactory<UUID>() {
                @Override
                public UUID getScreenOpeningData(ServerPlayerEntity viewer) {
                    return targetUuid;
                }

                @Override
                public Text getDisplayName() {
                    return Text.empty();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory pi, PlayerEntity viewer) {
                    return new AugmentScreenHandler(syncId, pi, inv, targetUuid);
                }
            });

            return ActionResult.SUCCESS;
        });
    }
}
