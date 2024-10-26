package dataaccess;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import chess.*;


import java.lang.reflect.Type;
import java.util.HashMap;

public class ChessPositionMapSerializer implements JsonSerializer<HashMap<ChessPosition, ChessPiece>> {
    @Override
    public JsonElement serialize(HashMap<ChessPosition, ChessPiece> map, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (var entry : map.entrySet()) {
            String key = entry.getKey().getRow() + "," + entry.getKey().getColumn(); // Convert to String for JSON
            jsonObject.add(key, context.serialize(entry.getValue()));
        }
        return jsonObject;
    }
}
