package eu.pb4.styledplayerlist.mixin;

import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerListS2CPacket.Entry.class)
public class PlayerListS2CPacketEntryMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/server/network/ServerPlayerEntity;)V", constant = @Constant(intValue = 1, ordinal = 0))
    private static int styledPlayerList$hideRealPlayer(int constant, ServerPlayerEntity player) {
        return ConfigManager.getConfig().configData.playerName.changeVisiblity ? (ConfigManager.getConfig().isPlayerHidden(player) ? 0 : 1) : constant;
    }
}
