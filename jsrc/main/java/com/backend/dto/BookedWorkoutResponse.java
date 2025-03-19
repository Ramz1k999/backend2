package com.backend.dto;

import com.backend.enums.ActivityPreference;
import com.backend.enums.WorkoutState;
import com.backend.utils.EnumTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record BookedWorkoutResponse(
        ActivityPreference activity,
        String clientId,
        String coachId,
        String dateTime,
        String description,
        String feedbackId,
        String id,
        String name,
        WorkoutState state
) {
    public BookedWorkoutResponse {
        if (activity == null || clientId == null || coachId == null || dateTime == null ||
                description == null || id == null || name == null || state == null) {
            throw new IllegalArgumentException("Missing or incomplete data.");
        }
    }

    public static BookedWorkoutResponse fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ActivityPreference.class, new EnumTypeAdapter<>(ActivityPreference.class))
                    .registerTypeAdapter(WorkoutState.class, new EnumTypeAdapter<>(WorkoutState.class))
                    .create();
            return gson.fromJson(jsonString, BookedWorkoutResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
