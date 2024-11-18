package org.polyfrost.evergreenhud.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.polyfrost.evergreenhud.ServerChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setServerData", at = @At("HEAD"))
    private void evergreen$readServerData(ServerData data, CallbackInfo ci) {
        if (data == null) EventManager.INSTANCE.post(new ServerChangedEvent(null, null, null));
        else EventManager.INSTANCE.post(new ServerChangedEvent(data.serverIP, data.serverName, data.serverMOTD));
    }
}
