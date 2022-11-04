package util.impl;

import com.google.gson.Gson;

public class JsonUtil {
    private static final Gson gson = new Gson();
    public static String getGsonFromCorridorDescription(CorridorDescription corridorDescription) {
        return gson.toJson(corridorDescription);
    }

    public static CorridorDescription getCorridorDescriptionFromGson(String gsonStr) {
        return gson.fromJson(gsonStr, CorridorDescription.class);
    }

    public static String getGsonFromTurnResult(TurnResult turnResult) {
        return gson.toJson(turnResult);
    }

    public static TurnResult getTurnResultFromJson(String gsonStr) {
        return gson.fromJson(gsonStr, TurnResult.class);
    }
}
