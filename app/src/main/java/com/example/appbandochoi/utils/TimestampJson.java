package com.example.appbandochoi.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampJson implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String datetimeStr = jsonElement.getAsString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

        try {
            return new Timestamp(sdf.parse(datetimeStr).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(timestamp.getTime());
    }
}