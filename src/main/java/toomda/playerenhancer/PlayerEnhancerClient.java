package toomda.playerenhancer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import toomda.playerenhancer.screen.AugmentScreen;
import toomda.playerenhancer.screen.AugmentScreenHandlerType;

public class PlayerEnhancerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(AugmentScreenHandlerType.TYPE, AugmentScreen::new);
    }
}
