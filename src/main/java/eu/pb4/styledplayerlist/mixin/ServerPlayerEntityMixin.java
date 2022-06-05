package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void spl_changePlayerListName(CallbackInfoReturnable<Text> cir) {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
                cir.setReturnValue(ConfigManager.getConfig().formatPlayerUsername((ServerPlayerEntity) (Object) this));
            }
        } catch (Exception e) {

        }
    }
}
