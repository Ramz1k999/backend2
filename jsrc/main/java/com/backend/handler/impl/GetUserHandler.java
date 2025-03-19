package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.backend.model.User;
import com.backend.service.UserService;
import com.google.gson.Gson;


import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class GetUserHandler implements EndpointHandler {

    private final UserService userService;
    private final Gson gson;

    @Inject
    public GetUserHandler(UserService userService, Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map<String, String> pathParameters = requestEvent.getPathParameters();

        if (pathParameters == null || !pathParameters.containsKey("userId") ||
                pathParameters.get("userId") == null || pathParameters.get("userId").isBlank()) {
            return createErrorResponse(400, "Missing userId parameter");
        }

        String userId = pathParameters.get("userId");
        User user = userService.getUserById(userId);

        if (user == null) {
            return createErrorResponse(404, "User not found");
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(user));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(gson.toJson(errorResponse));
    }
}