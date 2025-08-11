package org.polyfrost.evergreenhud.mixins.client;

import dev.deftu.textile.minecraft.MCTextHolder;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import org.polyfrost.evergreenhud.client.ServerChangedEvent;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class Mixin_Minecraft_ServerChangedEvent {

    //#if MC >= 1.20.4
    //$$ @Shadow public abstract ServerData getServerData();
    //#else
    @Shadow @Final private ServerData serverData;
    //#endif

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void evergreen$readServerData(CallbackInfo ci) {
        //#if MC >= 1.20.4
        //$$ ServerData data = this.getServerData();
        //#else
        ServerData data = this.serverData;
        //#endif
        if (data == null) {
            EventManager.INSTANCE.post(new ServerChangedEvent(null, null, null));
        } else {
            EventManager.INSTANCE.post(new ServerChangedEvent(
                    data.ip,
                    data.name,
                    MCTextHolder.convertFromVanilla(data.motd).asString()
            ));
        }
    }

}
