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
import net.minecraft.server.filter.FilteredMessage;
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
    private String spl_activeStyle = ConfigManager.getDefault();

    @Unique
    private long spl_ticker = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadData(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        try {
            NbtString nickname = PlayerDataApi.getGlobalDataFor(player, id("style"), NbtString.TYPE);

            if (nickname != null) {
                this.spl_setStyle(nickname.asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void updatePlayerList(CallbackInfo ci) {
        if (ConfigManager.isEnabled() && SPLHelper.shouldSendPlayerList(this.player)) {
            ConfigData config = ConfigManager.getConfig().configData;
            if (this.spl_ticker % config.updateRate == 0) {
                PlayerListStyle style = ConfigManager.getStyle(this.spl_activeStyle);
                this.sendPacket(new PlayerListHeaderS2CPacket(style.getHeader(this.player), style.getFooter(this.player)));
            }

            if (config.playerNameUpdateRate > 0 && this.spl_ticker % config.playerNameUpdateRate == 0) {
                this.spl_updateName();
            }

            this.spl_ticker += 1;
        }
    }



    @Inject(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;checkForSpam()V"))
    private void spl_onMessage(SignedMessage signedMessage, CallbackInfo ci) {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.updatePlayerNameEveryChatMessage) {
            this.spl_updateName();
        }
    }


    @Override
    public void spl_setStyle(String key) {
        if (ConfigManager.isEnabled()) {
            if (ConfigManager.styleExist(key)) {
                this.spl_activeStyle = key;
            } else {
                this.spl_activeStyle = ConfigManager.getConfig().configData.defaultStyle;
            }
        } else {
            this.spl_activeStyle = "default";
        }

        PlayerDataApi.setGlobalDataFor(this.player, id("style"), NbtString.of(this.spl_activeStyle));
    }


    @Override
    public String spl_getStyle() {
        return this.spl_activeStyle;
    }

    @Override
    public void spl_updateName() {
        try {
            if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
                PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, this.player);
                this.server.getPlayerManager().sendToAll(packet);
            }
        } catch (Exception e) {

        }
    }
}
