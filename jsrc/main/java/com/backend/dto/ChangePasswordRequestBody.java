package com.backend.dto;

import com.google.gson.Gson;

public record ChangePasswordRequestBody(
        String newPassword,
        String oldPassword,
        String userId
) {
    public static ChangePasswordRequestBody fromJson(String json) {
        return new Gson().fromJson(json, ChangePasswordRequestBody.class);
    }
}