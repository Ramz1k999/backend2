package com.backend.handler;

import com.backend.handler.impl.*;
import com.backend.service.CoachService;
import com.backend.service.UserService;
import com.backend.service.WorkoutService;
import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Module that provides handlers for the Dagger dependency injection framework.
 */
@Module
public class HandlersModule {

    /**
     * Provides a singleton instance of the Cognito client.
     */
    @Singleton
    @Provides
    public CognitoIdentityProviderClient provideCognitoClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(System.getenv("REGION")))
                .build();
    }

    /**
     * Provides the RouteNotImplementedHandler (404 Not Found handler).
     */
    @Singleton
    @Provides
    @Named("notFound")
    public EndpointHandler provideNotFoundHandler(Gson gson) {
        return new RouteNotImplementedHandler(gson);
    }

    /**
     * Provides the main general handler for routing requests.
     */
    @Singleton
    @Provides
    @Named("general")
    public EndpointHandler provideGeneralHandler(
            @Named("notFound") EndpointHandler notFoundHandler,
            Map<String, EndpointHandler> handlerMap) {
        return new GeneralHandler(notFoundHandler, handlerMap);
    }

    /**
     * Provides the PostSignInHandler.
     */
    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/auth/sign-in")
    public EndpointHandler providePostSignInHandler(CognitoIdentityProviderClient cognitoClient) {
        return new PostSignInHandler(cognitoClient);
    }

    /**
     * Provides the PostSignUpHandler.
     */
    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/auth/sign-up")
    public EndpointHandler providePostSignUpHandler(CognitoIdentityProviderClient cognitoClient, CoachService coachService, UserService userService) {
        return new PostSignUpHandler(cognitoClient, coachService, userService);
    }

    /**
     * Provides the PutUserPasswordHandler.
     */
    @Singleton
    @Provides
    @IntoMap
    @StringKey("PUT:/users/{userId}/password")
    public EndpointHandler providePutUserPasswordHandler(CognitoIdentityProviderClient cognitoClient, UserService userService) {
        return new PutUserPasswordHandler(cognitoClient, userService);
    }

    /**
     * Provides the GetRootHandler.
     */
    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/")
    public EndpointHandler provideGetRootHandler() {
        return new GetRootHandler();
    }

    /**
     * Provides the GetUserHandler.
     */
    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/users/{userId}")
    public EndpointHandler provideGetUserHandler(UserService userService, Gson gson) {
        return new GetUserHandler(userService, gson);
    }
    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/coaches/{coachId}")
    public EndpointHandler provideGetCoachHandler(CoachService coachService, Gson gson) {
        return new GetCoachHandler(coachService, gson);
    }


    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/workouts/booked/")
    public EndpointHandler provideGetBookedWorkoutsHandler(WorkoutService workoutService, Gson gson) {
        return new GetBookedWorkoutsHandler(workoutService, gson);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/workouts/available")
    public EndpointHandler provideGetAvailableWorkoutsHandler(WorkoutService workoutService, Gson gson) {
        return new GetAvailableWorkoutsHandler(workoutService, gson);
    }


    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/workouts/")
    public EndpointHandler provideBookWorkoutHandler(WorkoutService workoutService, Gson gson) {
        return new BookWorkoutHandler(workoutService, gson);
    }


}
