package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record FeedbackInfo(
        String clientImageUrl,
        String clientName,
        String date,
        String id,
        String message,
        String rating
) {
    public FeedbackInfo {
        if (clientImageUrl == null || clientName == null || date == null ||
                id == null || message == null || rating == null) {
            throw new IllegalArgumentException("Missing or incomplete feedback data.");
        }
    }

    public static FeedbackInfo fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, FeedbackInfo.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
