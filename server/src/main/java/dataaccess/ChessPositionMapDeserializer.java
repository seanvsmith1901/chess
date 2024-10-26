package dataaccess; // look I know that this looks weird but I had to do it to get my chess game it insert and desert correctly.

import com.google.gson.*;
import chess.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ChessPositionMapDeserializer implements JsonDeserializer<HashMap<ChessPosition, ChessPiece>> {
    @Override
    public HashMap<ChessPosition, ChessPiece> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        HashMap<ChessPosition, ChessPiece> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (String key : jsonObject.keySet()) {
            String[] parts = key.split(","); // Split the key back to row and column
            int row = Integer.parseInt(parts[0]);
            int column = Integer.parseInt(parts[1]);
            ChessPosition position = new ChessPosition(row, column);
            ChessPiece piece = context.deserialize(jsonObject.get(key), ChessPiece.class);
            map.put(position, piece);
        }
        return map;
    }
}
