package toomda.playerenhancer.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toomda.playerenhancer.net.OpenAugmentC2S;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void playerenhancer$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_MIDDLE || action != GLFW.GLFW_PRESS) return;
        if (client == null || client.player == null || client.world == null || client.getNetworkHandler() == null) return;
        if (client.currentScreen != null) return;

        HitResult hr = client.crosshairTarget;
        if (!(hr instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof PlayerEntity target)) return;

        ClientPlayNetworking.send(new OpenAugmentC2S(target.getId()));
    }
}
