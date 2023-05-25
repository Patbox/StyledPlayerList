package eu.pb4.styledplayerlist.config.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import eu.pb4.predicate.api.MinecraftPredicate;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.util.Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StyleData {
    @Nullable
    @SerializedName("require")
    public MinecraftPredicate require;

    @SerializedName("style_name")
    public String name = "Default";
    @SerializedName("update_tick_time")
    public int updateRate = 20;
    @SerializedName("list_header")
    public ElementList header = new ElementList();
    @SerializedName("list_footer")
    public ElementList footer = new ElementList();
    @SerializedName("hidden_in_commands")
    public boolean hidden = false;

    @Nullable
    @SerializedName("legacy_line_joining (Remove once updating!)")
    public Boolean legacyJoinBehaviour;


    public static class ElementList {
        @SerializedName("values")
        public List<List<String>> values = new ArrayList<>();
        @SerializedName("change_rate")
        public int changeRate = 1;

        public static class Serializer implements JsonSerializer<ElementList>, JsonDeserializer<ElementList> {
            static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();
            static final Type LIST_LIST_TYPE = new TypeToken<List<List<String>>>() {}.getType();
            @Override
            public ElementList deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                var e = new ElementList();
                if (jsonElement.isJsonArray()) {
                    e.values.add(jsonDeserializationContext.deserialize(jsonElement, LIST_TYPE));
                } else if (jsonElement.isJsonObject()) {
                    var obj = jsonElement.getAsJsonObject();
                    e.changeRate = obj.get("change_rate").getAsInt();
                    e.values = jsonDeserializationContext.deserialize(obj.get("values"), LIST_LIST_TYPE);
                } else if (jsonElement.isJsonPrimitive()) {
                    e.values.add(List.of(jsonElement.getAsString()));
                }

                return e;
            }

            @Override
            public JsonElement serialize(ElementList elementList, Type type, JsonSerializationContext jsonSerializationContext) {
                if (elementList.values.isEmpty()) {
                    return new JsonArray();
                } else if (elementList.values.size() == 1) {
                    return jsonSerializationContext.serialize(elementList.values.get(0));
                }
                var obj = new JsonObject();
                obj.addProperty("change_rate", elementList.changeRate);
                obj.add("values", jsonSerializationContext.serialize(elementList.values));

                return obj;
            }
        }
    }
}
