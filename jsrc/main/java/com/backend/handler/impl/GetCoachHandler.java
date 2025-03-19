package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.backend.model.Coach;
import com.backend.service.CoachService;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class GetCoachHandler implements EndpointHandler {

    private final CoachService coachService;
    private final Gson gson;

    @Inject
    public GetCoachHandler(CoachService coachService, Gson gson) {
        this.coachService = coachService;
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map<String, String> pathParameters = requestEvent.getPathParameters();

        if (pathParameters == null || !pathParameters.containsKey("coachId") ||
                pathParameters.get("coachId") == null || pathParameters.get("coachId").isBlank()) {
            return createErrorResponse(400, "Missing coachId parameter");
        }

        String coachId = pathParameters.get("coachId");
        Coach coach = coachService.getCoachById(coachId);

        if (coach == null) {
            return createErrorResponse(404, "Coach not found");
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(coach));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(gson.toJson(errorResponse));
    }
}
