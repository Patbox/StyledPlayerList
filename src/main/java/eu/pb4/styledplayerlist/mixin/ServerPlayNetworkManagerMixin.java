package eu.pb4.styledplayerlist.mixin;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eu.pb4.styledplayerlist.PlayerList.id;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkManagerMixin implements PlayerListViewerHolder {

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet);

    @Shadow @Final private MinecraftServer server;

    @Unique
    private String styledPlayerList$activeStyle = ConfigManager.getDefault();

    @Unique
    private long styledPlayerList$ticker = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void styledPlayerList$loadData(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        try {
            NbtString nickname = PlayerDataApi.getGlobalDataFor(player, id("style"), NbtString.TYPE);

            if (nickname != null) {
                this.styledPlayerList$setStyle(nickname.asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void styledPlayerList$updatePlayerList(CallbackInfo ci) {
        if (ConfigManager.isEnabled() && SPLHelper.shouldSendPlayerList(this.player)) {
            ConfigData config = ConfigManager.getConfig().configData;
            if (this.styledPlayerList$ticker % config.updateRate == 0) {
                PlayerListStyle style = ConfigManager.getStyle(this.styledPlayerList$activeStyle);
                this.sendPacket(new PlayerListHeaderS2CPacket(style.getHeader(this.player), style.getFooter(this.player)));
            }

            if (config.playerNameUpdateRate > 0 && this.styledPlayerList$ticker % config.playerNameUpdateRate == 0) {
                this.styledPlayerList$updateName();
            }

            this.styledPlayerList$ticker += 1;
        }
    }



    @Inject(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"))
    private void styledPlayerList$onMessage(SignedMessage signedMessage, CallbackInfo ci) {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.updatePlayerNameEveryChatMessage) {
            this.styledPlayerList$updateName();
        }
    }


    @Override
    public void styledPlayerList$setStyle(String key) {
        if (ConfigManager.isEnabled()) {
            if (ConfigManager.styleExist(key)) {
                this.styledPlayerList$activeStyle = key;
            } else {
                this.styledPlayerList$activeStyle = ConfigManager.getConfig().configData.defaultStyle;
            }
        } else {
            this.styledPlayerList$activeStyle = "default";
        }

        PlayerDataApi.setGlobalDataFor(this.player, id("style"), NbtString.of(this.styledPlayerList$activeStyle));
    }


    @Override
    public String styledPlayerList$getStyle() {
        return this.styledPlayerList$activeStyle;
    }

    @Override
    public void styledPlayerList$updateName() {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
                PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, this.player);
                this.server.getPlayerManager().sendToAll(packet);
            }
        } catch (Exception e) {

        }
    }
}
