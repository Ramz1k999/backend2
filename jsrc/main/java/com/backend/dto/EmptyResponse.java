package com.backend.dto;

public record EmptyResponse() {
    // No fields required since this is just a placeholder for empty responses

    public static final EmptyResponse INSTANCE = new EmptyResponse();
}
