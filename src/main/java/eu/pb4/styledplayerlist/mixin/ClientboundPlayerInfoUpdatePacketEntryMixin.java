package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientboundPlayerInfoUpdatePacket.Entry.class)
public class ClientboundPlayerInfoUpdatePacketEntryMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/server/level/ServerPlayer;)V", constant = @Constant(intValue = 1, ordinal = 0))
    private static int styledPlayerList$hideRealPlayer(int constant, ServerPlayer player) {
        return ConfigManager.getConfig().configData.playerName.changeVisiblity ? (ConfigManager.getConfig().isPlayerHidden(player) ? 0 : 1) : constant;
    }
}
