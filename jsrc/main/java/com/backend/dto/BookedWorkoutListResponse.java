package com.backend.dto;

import com.backend.enums.ActivityPreference;
import com.backend.enums.WorkoutState;
import com.backend.utils.EnumTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public record BookedWorkoutListResponse(
        List<BookedWorkoutResponse> content
) {
    public BookedWorkoutListResponse {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content list cannot be null or empty.");
        }
    }

    public static BookedWorkoutListResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ActivityPreference.class, new EnumTypeAdapter<>(ActivityPreference.class))
                    .registerTypeAdapter(WorkoutState.class, new EnumTypeAdapter<>(WorkoutState.class))
                    .create();
            return gson.fromJson(jsonString, BookedWorkoutListResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
