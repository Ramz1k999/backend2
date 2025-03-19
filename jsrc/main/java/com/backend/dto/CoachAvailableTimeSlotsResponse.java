package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public record CoachAvailableTimeSlotsResponse(
        List<String> content
) {
    public CoachAvailableTimeSlotsResponse {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content list cannot be null or empty.");
        }
    }

    public static CoachAvailableTimeSlotsResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, CoachAvailableTimeSlotsResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
