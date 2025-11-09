package toomda.playerenhancer;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toomda.playerenhancer.logic.StatApplier;
import toomda.playerenhancer.logic.TargetAugmentState;
import toomda.playerenhancer.net.OpenAugmentC2S;
import toomda.playerenhancer.screen.AugmentScreenHandlerType;
import toomda.playerenhancer.logic.RightClickHandler;

import java.util.UUID;

public class PlayerEnhancer implements ModInitializer {
	public static final String MOD_ID = "playerenhancer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(OpenAugmentC2S.ID, OpenAugmentC2S.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(OpenAugmentC2S.ID, (payload, context) -> {
			var viewer = context.player();
			context.server().execute(() -> openForTarget(viewer, payload.targetEntityId()));
		});


		AugmentScreenHandlerType.register();
		StatApplier.register();
		LOGGER.info("Hello Fabric world!");
	}

	private static void openForTarget(ServerPlayerEntity viewer, int entityId) {
		var world = viewer.getServerWorld();
		var e = world.getEntityById(entityId);
		if (!(e instanceof PlayerEntity target)) {
			return;
		}

		var dist2 = target.squaredDistanceTo(viewer);
		if (dist2 > 10 * 10) {
			return;
		}

		var state = TargetAugmentState.get(world);
		var inv = state.getOrCreate(target.getUuidAsString());
		var targetUuid = target.getUuid();

		viewer.openHandledScreen(new net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory<UUID>() {
			@Override public UUID getScreenOpeningData(ServerPlayerEntity v) { return targetUuid; }
			@Override public net.minecraft.text.Text getDisplayName() { return net.minecraft.text.Text.empty(); }
			@Override public net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory pi, net.minecraft.entity.player.PlayerEntity v) {
				return new toomda.playerenhancer.screen.AugmentScreenHandler(syncId, pi, inv, targetUuid);
			}
		});
	}

}