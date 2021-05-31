package eu.pb4.styledplayerlist.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.styledplayerlist.Helper;
import eu.pb4.styledplayerlist.access.SPEPlayerList;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements SPEPlayerList {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    private String activePlayerListStyle = ConfigManager.getDefault();
    private long activeListUpdateTicker = 0;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readActiveStyle(NbtCompound tag, CallbackInfo ci) {
        if (ConfigManager.isEnabled()) {
            if (tag.contains("playerListStyle", NbtType.STRING)) {
                String key = tag.getString("playerListStyle");
                if (ConfigManager.styleExist(key)) {
                    this.activePlayerListStyle = key;
                } else {
                    this.activePlayerListStyle = ConfigManager.getConfig().configData.defaultStyle;
                }
            } else {
                this.activePlayerListStyle = ConfigManager.getConfig().configData.defaultStyle;
            }
        } else {
            if (tag.contains("playerListStyle", NbtType.STRING)) {
                this.activePlayerListStyle = tag.getString("playerListStyle");
            } else {
                this.activePlayerListStyle = "default";
            }
        }

    }


    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeActiveStyle(NbtCompound tag, CallbackInfo ci) {
        if (this.activePlayerListStyle != null) {
            tag.putString("playerListStyle", this.activePlayerListStyle);
        }
    }

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void updatePlayerList(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (ConfigManager.isEnabled() && Helper.shouldSendPlayerList(player)) {
            ConfigData config = ConfigManager.getConfig().configData;
            if (this.activeListUpdateTicker % config.updateRate == 0) {
                PlayerListStyle style = ConfigManager.getStyle(this.activePlayerListStyle);
                this.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(style.getHeader((ServerPlayerEntity) (Object) this), style.getFooter((ServerPlayerEntity) (Object) this)));
            }

            if (config.playerNameUpdateRate > 0 && this.activeListUpdateTicker % config.playerNameUpdateRate == 0) {
                this.styledPlayerList$updatePlayerListName();
            }

            this.activeListUpdateTicker += 1;
        }
    }
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyCustomData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.activePlayerListStyle = ((SPEPlayerList) oldPlayer).styledPlayerList$getActivePlayerListStyle();
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void changePlayerListName(CallbackInfoReturnable<Text> cir) {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
            cir.setReturnValue(ConfigManager.getConfig().formatPlayerUsername((ServerPlayerEntity) (Object) this));
        }
    }


    @Override
    public void styledPlayerList$setPlayerListStyle(String key) {
        if (ConfigManager.isEnabled()) {
            if (ConfigManager.styleExist(key)) {
                this.activePlayerListStyle = key;
                return;
            }
            this.activePlayerListStyle = ConfigManager.getConfig().configData.defaultStyle;
        } else {
            this.activePlayerListStyle = "default";
        }
    }


    @Override
    public String styledPlayerList$getActivePlayerListStyle() {
        return this.activePlayerListStyle;
    }

    @Override
    public void styledPlayerList$updatePlayerListName() {
        if (ConfigManager.isEnabled() && ConfigManager.getConfig().configData.changePlayerName) {
            PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) (Object) this);
            this.getServer().getPlayerManager().sendToAll(packet);
        }
    }

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
}
