package com.backend.dto;

import com.backend.enums.ActivityPreference;
import com.backend.enums.FitnessTarget;
import com.backend.enums.Role;
import com.backend.utils.EnumTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public record UserProfileResponseBody(
        String about,
        String email,
        List<String> fileUrls,
        String firstName,
        String userId,
        String imageUrl,
        String lastName,
        ActivityPreference preferableActivity,
        Role role,
        List<ActivityPreference> specializations,
        FitnessTarget target
) {
    public UserProfileResponseBody {
        if (email == null || firstName == null || lastName == null || userId == null || role == null) {
            throw new IllegalArgumentException("Missing required user profile data.");
        }
    }

    public static UserProfileResponseBody fromJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(FitnessTarget.class, new EnumTypeAdapter<>(FitnessTarget.class))
                    .registerTypeAdapter(ActivityPreference.class, new EnumTypeAdapter<>(ActivityPreference.class))
                    .registerTypeAdapter(Role.class, new EnumTypeAdapter<>(Role.class))
                    .create();
            return gson.fromJson(jsonString, UserProfileResponseBody.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
