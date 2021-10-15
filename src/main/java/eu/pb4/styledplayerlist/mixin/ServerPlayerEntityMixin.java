package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.access.SPEOldStyleData;
import eu.pb4.styledplayerlist.config.ConfigManager;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements SPEOldStyleData {
    @Unique public String spl_oldStyle = null;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void spl_readOldStyleData(NbtCompound tag, CallbackInfo ci) {
        if (ConfigManager.isEnabled()) {
            if (tag.contains("playerListStyle", NbtType.STRING)) {
                String key = tag.getString("playerListStyle");
                if (ConfigManager.styleExist(key)) {
                    this.spl_oldStyle = key;
                }
            }
        }
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void spl_changePlayerListName(CallbackInfoReturnable<Text> cir) {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
                cir.setReturnValue(ConfigManager.getConfig().formatPlayerUsername((ServerPlayerEntity) (Object) this));
            }
        } catch (Exception e) {

        }
    }

    @Override
    public String spl_getOldStyle() {
        return this.spl_oldStyle;
    }
}
