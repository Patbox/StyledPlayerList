package eu.pb4.styledplayerlist;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class Helper {
    public static Set<PlayerList.ModCompatibility> COMPATIBILITY = new HashSet<>();

    public static boolean shouldSendPlayerList(ServerPlayerEntity player) {
        for (PlayerList.ModCompatibility mod : COMPATIBILITY) {
            boolean value = mod.check(player);

            if (value) {
                return false;
            }
        }
        return true;
    }
}
