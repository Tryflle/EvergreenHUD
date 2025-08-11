package org.polyfrost.evergreenhud.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.polyfrost.evergreenhud.client.ServerChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.16.5
//$$ import dev.deftu.textile.minecraft.MCTextHolder;
//#endif

@Mixin(Minecraft.class)
public class Mixin_Minecraft_ServerChangedEvent {

    @Inject(
            //#if MC >= 1.19.2
            //$$ method = "setCurrentServerEntry(Lnet/minecraft/client/network/ServerInfo;)V",
            //#else
            method = "setServerData",
            //#endif
            at = @At("HEAD")
    )
    private void evergreen$readServerData(ServerData data, CallbackInfo ci) {
        if (data == null) {
            EventManager.INSTANCE.post(new ServerChangedEvent(null, null, null));
        } else {
            EventManager.INSTANCE.post(new ServerChangedEvent(
                    data.serverIP,
                    data.serverName,
                    //#if MC >= 1.16.5
                    //$$ MCTextHolder.convertFromVanilla(data.motd).asString()
                    //#else
                    data.serverMOTD
                    //#endif
            ));
        }
    }

}
