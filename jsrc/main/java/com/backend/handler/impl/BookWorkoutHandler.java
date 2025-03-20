package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.dto.BookNewWorkoutRequestBody;
import com.backend.dto.BookedWorkoutResponse;
import com.backend.handler.EndpointHandler;
import com.backend.model.Workout;
import com.backend.service.WorkoutService;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class BookWorkoutHandler implements EndpointHandler {

    private final WorkoutService workoutService;
    private final Gson gson;

    @Inject
    public BookWorkoutHandler(WorkoutService workoutService, Gson gson) {
        this.workoutService = workoutService;
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        if (requestEvent.getBody() == null || requestEvent.getBody().isBlank()) {
            return createErrorResponse(400, "Request body is missing");
        }

        // Deserialize request body into DTO
        BookNewWorkoutRequestBody requestBody = gson.fromJson(requestEvent.getBody(), BookNewWorkoutRequestBody.class);

        if (requestBody.getClientId() == null || requestBody.getCoachId() == null ||
            requestBody.getDate() == null || requestBody.getTimeSlot() == null) {
            return createErrorResponse(400, "Missing required fields (clientId, coachId, date, timeSlot)");
        }

        // Attempt to book the workout
        Workout bookedWorkout = workoutService.bookWorkout(requestBody);

        if (bookedWorkout == null) {
            return createErrorResponse(404, "Workout not found or already booked");
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(new BookedWorkoutResponse(bookedWorkout)));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(gson.toJson(errorResponse));
    }
}
