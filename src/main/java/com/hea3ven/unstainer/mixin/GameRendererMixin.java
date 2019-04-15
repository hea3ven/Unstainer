package com.hea3ven.unstainer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;

import com.hea3ven.unstainer.client.gui.hud.TestProgressHud;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    private MinecraftClient client;

    private final TestProgressHud testProgressHud = new TestProgressHud();

    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE",
            target = "net/minecraft/client/render/GameRenderer.renderFloatingItem(IIF)V"))
    private void onRender(float ticksDelta, long time, boolean render, CallbackInfo info) {
        testProgressHud.draw(client);
    }
}
