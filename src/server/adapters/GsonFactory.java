package server.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonFactory {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>)
                                (src, typeOfSrc, context) ->
                                        new JsonPrimitive(src.format(formatter)))
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>)
                                (json, typeOfT, context) ->
                                        LocalDateTime.parse(json.getAsString(), formatter))
                .registerTypeAdapter(
                        Duration.class,
                        (JsonSerializer<Duration>)
                                (src, typeOfSrc, context) ->
                                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(
                        Duration.class,
                        (JsonDeserializer<Duration>)
                                (json, typeOfT, context) ->
                                        Duration.parse(json.getAsString()))
                .create();
    }
}
