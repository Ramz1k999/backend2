package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record MessageResponse(
        String message
) {
    public MessageResponse {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty.");
        }
    }

    public static MessageResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, MessageResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
