package eu.pb4.styledplayerlist.access;

import eu.pb4.styledplayerlist.config.PlayerListStyle;

public interface PlayerListViewerHolder {
    void styledPlayerList$setStyle(String key);
    String styledPlayerList$getStyle();
    void styledPlayerList$updateName();
    void styledPlayerList$reloadStyle();
    int styledPlayerList$getAndIncreaseAnimationTick();

    PlayerListStyle styledPlayerList$getStyleObject();
}
