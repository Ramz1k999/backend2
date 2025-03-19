package com.backend.dto;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public record SignIn(String email, String password) {

    public SignIn {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Missing or incomplete data.");
        }
    }

    public static SignIn fromJson(String jsonString) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, SignIn.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
