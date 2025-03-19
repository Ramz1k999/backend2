package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record FeedbackRequestBody(
        String clientId,
        String coachId,
        String comment,
        String rating,
        String workoutId
) {
    public FeedbackRequestBody {
        if (clientId == null || coachId == null || comment == null || rating == null || workoutId == null) {
            throw new IllegalArgumentException("Missing or incomplete feedback request data.");
        }
    }

    public static FeedbackRequestBody fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, FeedbackRequestBody.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
