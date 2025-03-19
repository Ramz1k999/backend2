package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public record CoachFeedbackPageResponse(
        List<FeedbackInfo> content,
        int currentPage,
        long totalElements,
        int totalPages
) {
    public CoachFeedbackPageResponse {
        if (content == null || currentPage < 0 || totalElements < 0 || totalPages < 0) {
            throw new IllegalArgumentException("Missing or invalid pagination data.");
        }
    }

    public static CoachFeedbackPageResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, CoachFeedbackPageResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
