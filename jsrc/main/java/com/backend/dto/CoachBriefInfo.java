package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record CoachBriefInfo(
        String id,
        String imageUrl,
        String motivationPitch,
        String name,
        String rating,
        String summary
) {
    public CoachBriefInfo {
        if (id == null || imageUrl == null || motivationPitch == null ||
                name == null || rating == null || summary == null) {
            throw new IllegalArgumentException("Missing or incomplete coach data.");
        }
    }

    public static CoachBriefInfo fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, CoachBriefInfo.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
