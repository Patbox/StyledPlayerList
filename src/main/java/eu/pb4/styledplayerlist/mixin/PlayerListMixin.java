package eu.pb4.styledplayerlist.mixin;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledplayerlist.PlayerList;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;

@Mixin(net.minecraft.server.players.PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow public abstract void broadcastAll(Packet<?> packet);

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"))
    private void sendStuff(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        var handler = player.connection;
        ((PlayerListViewerHolder) handler).styledPlayerList$setupRightText();
        var packet = new ClientboundSetScorePacket(player.getScoreboardName(), PlayerList.OBJECTIVE_NAME, 0, Optional.empty(), Optional.of(new FixedFormat(
                ConfigManager.getConfig().formatPlayerRightText(player))
        ));
        this.broadcastAll(packet);
        handler.send(packet);
        var context = PlaceholderContext.of(player, SPLHelper.PLAYER_LIST_VIEW);
        var x = ((PlayerListViewerHolder) handler).styledPlayerList$getAndIncreaseAnimationTick();
        var style = ((PlayerListViewerHolder) handler).styledPlayerList$getStyleObject();
        handler.send(new ClientboundTabListPacket(style.getHeader(context, x), style.getFooter(context, x)));
    }
}
