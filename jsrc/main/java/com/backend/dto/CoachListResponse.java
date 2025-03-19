package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public record CoachListResponse(
        List<CoachBriefInfo> content
) {
    public CoachListResponse {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content list cannot be null or empty.");
        }
    }

    public static CoachListResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, CoachListResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
