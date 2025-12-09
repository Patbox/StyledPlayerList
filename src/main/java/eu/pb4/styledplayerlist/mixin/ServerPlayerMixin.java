package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    private void styledPlayerList$changePlayerListName(CallbackInfoReturnable<Component> cir) {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.playerName.changePlayerName) {
                cir.setReturnValue(ConfigManager.getConfig().formatPlayerUsername((ServerPlayer) (Object) this));
            }
        } catch (Exception ignored) {

        }
    }

    @Inject(method = "getTabListOrder", at = @At("HEAD"), cancellable = true)
    private void styledPlayerList$getPlayerListIndex(CallbackInfoReturnable<Integer> cir) {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.playerName.modifyListOrder) {
                var x = ConfigManager.getConfig().sortingIndex((ServerPlayer) (Object) this);
                if (x != null) {
                    cir.setReturnValue(x);
                }
            }
        } catch (Exception ignored) {

        }
    }
}
