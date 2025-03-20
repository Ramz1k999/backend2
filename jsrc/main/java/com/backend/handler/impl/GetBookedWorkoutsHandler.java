package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.backend.model.Workout;
import com.backend.service.WorkoutService;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBookedWorkoutsHandler implements EndpointHandler {

    private final WorkoutService workoutService;
    private final Gson gson;

    @Inject
    public GetBookedWorkoutsHandler(WorkoutService workoutService, Gson gson) {
        this.workoutService = workoutService;
        this.gson = gson;
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map<String, String> queryParameters = requestEvent.getQueryStringParameters();

        if (pathParameters == null || !pathParameters.containsKey("userId") ||
                pathParameters.get("userId") == null || queryParameters.get("userId").isBlank()) {
            return createErrorResponse(400, "Missing userId parameter");
        }

        String userId = queryParameters.get("userId");
        List<Workout> workouts = workoutService.getWorkoutsByUserId(userId);

        if (workouts == null || workouts.isEmpty()) {
            return createErrorResponse(404, "No workouts found for userId: " + userId);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(workouts));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(gson.toJson(errorResponse));
    }
}
