package eu.pb4.styledplayerlist;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.command.Commands;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

public class PlayerList implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Player List");
	public static final String ID = "styledplayerlist";

	@Override
	public void onInitialize() {
		GenericModInfo.build(FabricLoader.getInstance().getModContainer(ID).get());
		Commands.register();
		ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
			ConfigManager.loadConfig();
		});

		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			CardboardWarning.checkAndAnnounce();
			//MicroScheduler.get(s).scheduleRepeating(50, () -> tick(s));
		});
	}

	private void tick(MinecraftServer server) {
		if (ConfigManager.isEnabled()) {
			ConfigData config = ConfigManager.getConfig().configData;
			for (var player : server.getPlayerManager().getPlayerList()) {
				var x = System.nanoTime();
				if (!SPLHelper.shouldSendPlayerList(player) || player.networkHandler == null) {
					continue;
				}
				var tick = server.getTicks();
				var holder = (PlayerListViewerHolder) player.networkHandler;

				var style = holder.styledPlayerList$getStyleObject();

				if (tick % style.updateRate == 0) {
					var context = PlaceholderContext.of(player, SPLHelper.PLAYER_LIST_VIEW);
					var animationTick = holder.styledPlayerList$getAndIncreaseAnimationTick();
					player.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(style.getHeader(context, animationTick), style.getFooter(context, animationTick)));
				}

				if (config.playerName.playerNameUpdateRate > 0 && tick % config.playerName.playerNameUpdateRate == 0) {
					holder.styledPlayerList$updateName();
				}
				player.sendMessage(Text.literal(tick + " | " + ((System.nanoTime() - x) / 1000000f)), true);
			}
		}
	}

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	public static final Event<PlayerList.PlayerListStyleLoad> PLAYER_LIST_STYLE_LOAD = EventFactory.createArrayBacked(PlayerList.PlayerListStyleLoad.class, (callbacks) -> (styleHelper) -> {
		for(PlayerListStyleLoad callback : callbacks ) {
			callback.onPlayerListUpdate(styleHelper);
		}

	});

	@FunctionalInterface
	public interface PlayerListStyleLoad {
		void onPlayerListUpdate(StyleHelper styleHelper);
	}


	public record StyleHelper(LinkedHashMap<String, PlayerListStyle> styles) {
		public void addStyle(PlayerListStyle style) {
			this.styles.put(style.id, style);
		}

		public void removeStyle(PlayerListStyle style) {
			this.styles.remove(style.id, style);
		}
	}


	public static String getPlayersStyle(ServerPlayerEntity player) {
		return ((PlayerListViewerHolder) player.networkHandler).styledPlayerList$getStyle();
	}

	public static void setPlayersStyle(ServerPlayerEntity player, String key) {
		((PlayerListViewerHolder) player).styledPlayerList$setStyle(key);
	}

	public static void addUpdateSkipCheck(ModCompatibility check) {
		SPLHelper.COMPATIBILITY.add(check);
	}

	public interface ModCompatibility {
		boolean check(ServerPlayerEntity player);
	}
}
