package toomda.playerenhancer.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import toomda.playerenhancer.PlayerEnhancer;

public final class AugmentScreenHandlerType {
    public static ScreenHandlerType<AugmentScreenHandler> TYPE;

    public static void register() {
        TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(PlayerEnhancer.MOD_ID, "augment"),
                new ExtendedScreenHandlerType<>(
                        AugmentScreenHandler::new,
                        Uuids.PACKET_CODEC
                )
        );
    }
}
