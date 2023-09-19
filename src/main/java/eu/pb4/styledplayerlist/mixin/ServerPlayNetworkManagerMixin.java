package eu.pb4.styledplayerlist.mixin;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.styledplayerlist.PlayerList;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.DefaultValues;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

import static eu.pb4.styledplayerlist.PlayerList.id;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkManagerMixin extends ServerCommonNetworkHandler implements PlayerListViewerHolder {

    @Shadow public ServerPlayerEntity player;

    @Unique
    private String styledPlayerList$activeStyle = ConfigManager.getDefault();

    @Unique
    private PlayerListStyle styledPlayerList$style = DefaultValues.EMPTY_STYLE;

    @Unique
    private int styledPlayerList$animationTick = 0;

    public ServerPlayNetworkManagerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void styledPlayerList$loadData(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        try {
            NbtString style = PlayerDataApi.getGlobalDataFor(player, id("style"), NbtString.TYPE);

            if (style != null) {
                this.styledPlayerList$setStyle(style.asString());
            } else {
                this.styledPlayerList$reloadStyle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void styledPlayerList$updatePlayerList(CallbackInfo ci) {
        if (ConfigManager.isEnabled() && SPLHelper.shouldSendPlayerList(this.player)) {
            var tick = this.server.getTicks();
            ConfigData config = ConfigManager.getConfig().configData;

            if (tick % this.styledPlayerList$style.updateRate == 0) {
                var context = PlaceholderContext.of(this.player, SPLHelper.PLAYER_LIST_VIEW);
                this.sendPacket(new PlayerListHeaderS2CPacket(this.styledPlayerList$style.getHeader(context, this.styledPlayerList$animationTick), this.styledPlayerList$style.getFooter(context, this.styledPlayerList$animationTick)));
                this.styledPlayerList$animationTick += 1;
            }

            if (config.playerName.playerNameUpdateRate > 0 && tick % config.playerName.playerNameUpdateRate == 0) {
                this.styledPlayerList$updateName();
            }

        }
    }

    @Inject(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"))
    private void styledPlayerList$onMessage(SignedMessage signedMessage, CallbackInfo ci) {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.playerName.updatePlayerNameEveryChatMessage) {
            this.styledPlayerList$updateName();
        }
    }

    @Override
    public void styledPlayerList$setStyle(String key) {
        if (ConfigManager.isEnabled()) {
            if (ConfigManager.styleExist(key)) {
                this.styledPlayerList$activeStyle = key;
            } else {
                this.styledPlayerList$activeStyle = ConfigManager.getDefault();
            }
        } else {
            this.styledPlayerList$activeStyle = "default";
        }
        styledPlayerList$reloadStyle();

        PlayerDataApi.setGlobalDataFor(this.player, id("style"), NbtString.of(this.styledPlayerList$activeStyle));
    }


    @Override
    public String styledPlayerList$getStyle() {
        return this.styledPlayerList$activeStyle;
    }

    @Override
    public void styledPlayerList$updateName() {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.playerName.changePlayerName) {
                PlayerListS2CPacket packet = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.UPDATE_LISTED), List.of(this.player));
                this.server.getPlayerManager().sendToAll(packet);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void styledPlayerList$reloadStyle() {
        var style = ConfigManager.getStyle(this.styledPlayerList$activeStyle);
        if (style != this.styledPlayerList$style) {
            this.styledPlayerList$style = style;
            this.styledPlayerList$animationTick = 0;
        }
    }

    @Override
    public int styledPlayerList$getAndIncreaseAnimationTick() {
        return this.styledPlayerList$animationTick++;
    }

    @Override
    public PlayerListStyle styledPlayerList$getStyleObject() {
        return this.styledPlayerList$style;
    }
}
