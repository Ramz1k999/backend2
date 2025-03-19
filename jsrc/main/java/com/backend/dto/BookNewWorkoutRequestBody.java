package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record BookNewWorkoutRequestBody(
        String clientId,
        String coachId,
        String date,
        String timeSlot
) {
    public BookNewWorkoutRequestBody {
        if (clientId == null || coachId == null || date == null || timeSlot == null) {
            throw new IllegalArgumentException("Missing or incomplete data.");
        }
    }

    public static BookNewWorkoutRequestBody fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, BookNewWorkoutRequestBody.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
