package toomda.playerenhancer;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toomda.playerenhancer.logic.StatApplier;
import toomda.playerenhancer.screen.AugmentScreenHandlerType;
import toomda.playerenhancer.logic.RightClickHandler;

public class PlayerEnhancer implements ModInitializer {
	public static final String MOD_ID = "playerenhancer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		AugmentScreenHandlerType.register();
		RightClickHandler.register();
		StatApplier.register();
		LOGGER.info("Hello Fabric world!");
	}
}