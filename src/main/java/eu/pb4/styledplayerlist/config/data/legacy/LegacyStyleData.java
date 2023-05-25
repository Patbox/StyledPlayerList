package eu.pb4.styledplayerlist.config.data.legacy;

import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledplayerlist.config.data.StyleData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class LegacyStyleData {
    public String id = "default";
    public String name = "Default";
    public List<String> header = new ArrayList<>();
    public List<String> footer = new ArrayList<>();
    public boolean hidden = false;
    public String permission = "";

    public StyleData convert(@Nullable LegacyConfigData configData) {
        var style = new StyleData();
        style.header.values.add(this.header);
        style.footer.values.add(this.footer);
        style.name = this.name;
        style.legacyJoinBehaviour = Boolean.TRUE;

        if (!this.permission.isEmpty()) {
            style.require = BuiltinPredicates.modPermissionApi(this.permission);
        }

        if (configData != null) {
            style.updateRate = configData.updateRate;
        }

        return style;
    }
}
