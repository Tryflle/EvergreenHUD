package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private static int debugFPS;

    @Inject(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;debug:Ljava/lang/String;"))
    private void evergreen$readFPS(CallbackInfo ci) {
        FPSHud.update(debugFPS);
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void evergreen$readCPSLeft(CallbackInfo ci) {
        CPS.onLeftClick();
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"))
    private void evergreen$readCPSRight(CallbackInfo ci) {
        CPS.onRightClick();
    }

    @Inject(method = "setServerData", at = @At("HEAD"))
    private void evergreen$readServerData(ServerData data, CallbackInfo ci) {
        ServerHud.update(data.serverIP, data.serverName, data.serverMOTD);
    }
}
