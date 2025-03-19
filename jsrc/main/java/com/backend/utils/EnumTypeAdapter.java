package com.backend.utils;

import com.google.gson.*;
import java.lang.reflect.Type;

public class EnumTypeAdapter<T extends Enum<T>> implements JsonDeserializer<T> {
    private final Class<T> enumClass;

    public EnumTypeAdapter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new JsonParseException("Invalid enum value: " + value + " for enum: " + enumClass.getSimpleName());
    }
}