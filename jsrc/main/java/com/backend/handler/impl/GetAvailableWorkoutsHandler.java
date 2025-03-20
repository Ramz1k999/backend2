package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.backend.model.Workout;
import com.backend.service.WorkoutService;
import com.google.gson.Gson;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class GetAvailableWorkoutsHandler implements EndpointHandler {

    private final WorkoutService workoutService;
    private final Gson gson;

    @Inject
    public GetAvailableWorkoutsHandler(WorkoutService workoutService, Gson gson) {
        this.workoutService = workoutService;
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        List<Workout> availableWorkouts = workoutService.getAvailableWorkouts();

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(availableWorkouts));
    }
}
