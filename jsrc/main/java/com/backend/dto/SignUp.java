package com.backend.dto;

import com.backend.enums.ActivityPreference;
import com.backend.enums.FitnessTarget;
import com.backend.utils.EnumTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public record SignUp(
        String firstName,
        String lastName,
        String email,
        String password,
        FitnessTarget target,
        ActivityPreference preferableActivity
) {
    public SignUp {
        if (firstName == null || lastName == null || email == null || password == null ||
                target == null || preferableActivity == null) {
            throw new IllegalArgumentException("Missing or incomplete data.");
        }
    }

    public static SignUp fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(FitnessTarget.class, new EnumTypeAdapter<>(FitnessTarget.class))
                    .registerTypeAdapter(ActivityPreference.class, new EnumTypeAdapter<>(ActivityPreference.class))
                    .create();
            return gson.fromJson(jsonString, SignUp.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}