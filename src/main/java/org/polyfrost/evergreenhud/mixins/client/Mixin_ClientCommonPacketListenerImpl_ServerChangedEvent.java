package org.polyfrost.evergreenhud.mixins.client;

import dev.deftu.textile.CollapseMode;
import dev.deftu.textile.minecraft.MCText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import org.polyfrost.evergreenhud.client.ServerChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class Mixin_ClientCommonPacketListenerImpl_ServerChangedEvent {
    @Inject(
            method = "<init>",
            at = @At("HEAD")
    )
    private static void evergreen$readServerData(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        var data = commonListenerCookie.serverData();
        if (data == null) {
            EventManager.INSTANCE.post(new ServerChangedEvent(null, null, null));
        } else {
            EventManager.INSTANCE.post(new ServerChangedEvent(
                    data.ip,
                    data.name,
                    MCText.wrap(data.motd).collapseToString(CollapseMode.SCOPED)
            ));
        }
    }
}
