package eu.pb4.styledplayerlist.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledplayerlist.PlayerList;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.config.ConfigManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.scoreboard.number.FixedNumberFormat;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract void sendToAll(Packet<?> packet);

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendWorldInfo(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/world/ServerWorld;)V"))
    private void sendStuff(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        var handler = player.networkHandler;
        ((PlayerListViewerHolder) handler).styledPlayerList$setupRightText();
        var packet = new ScoreboardScoreUpdateS2CPacket(player.getNameForScoreboard(), PlayerList.OBJECTIVE_NAME, 0, Optional.empty(), Optional.of(new FixedNumberFormat(
                ConfigManager.getConfig().formatPlayerRightText(player))
        ));
        this.sendToAll(packet);
        handler.sendPacket(packet);
        var context = PlaceholderContext.of(player, SPLHelper.PLAYER_LIST_VIEW);
        var x = ((PlayerListViewerHolder) handler).styledPlayerList$getAndIncreaseAnimationTick();
        var style = ((PlayerListViewerHolder) handler).styledPlayerList$getStyleObject();
        handler.sendPacket(new PlayerListHeaderS2CPacket(style.getHeader(context, x), style.getFooter(context, x)));
    }
}
