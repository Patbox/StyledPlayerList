package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.access.SPEPlayerList;
import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkManagerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "method_31286", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void updatePlayerNameAfterMessage(String string, CallbackInfo ci) {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().updatePlayerNameEveryChatMessage) {
            ((SPEPlayerList) this.player).styledPlayerList$updatePlayerListName();
        }
    }
}
