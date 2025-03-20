package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.backend.service.WorkoutService;
import com.backend.dto.BookedWorkoutListResponse;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CancelWorkoutHandler implements EndpointHandler {

    private final WorkoutService workoutService;
    private final Gson gson;

    @Inject
    public CancelWorkoutHandler(WorkoutService workoutService, Gson gson) {
        this.workoutService = workoutService;
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map<String, String> pathParameters = requestEvent.getPathParameters();

        if (pathParameters == null || !pathParameters.containsKey("workoutId")) {
            return createErrorResponse(400, "Missing workoutId in path");
        }

        String workoutId = pathParameters.get("workoutId");

        BookedWorkoutListResponse response = workoutService.cancelWorkout(workoutId);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(response));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(gson.toJson(errorResponse));
    }
}
