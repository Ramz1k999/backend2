package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


import com.backend.auth.CognitoSupport;
import com.backend.dto.SignUp;
import com.backend.enums.Role;
import com.backend.handler.EndpointHandler;
import com.backend.model.User;
import com.backend.service.CoachService;
import com.backend.service.UserService;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import javax.inject.Inject;
import java.util.Optional;

public class PostSignUpHandler extends CognitoSupport implements EndpointHandler {
    private final CoachService coachService;
    private final UserService userService;

    @Inject
    public PostSignUpHandler(CognitoIdentityProviderClient cognitoClient, CoachService coachService, UserService userService) {
        super(cognitoClient);
        this.coachService = coachService;
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            SignUp signUp = SignUp.fromJson(requestEvent.getBody());
            AdminCreateUserResponse createResponse;
            try {
                createResponse = adminCreateUser(signUp);
            } catch (UsernameExistsException e) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody(new JSONObject()
                                .put("error", "A user with this email already exists")
                                .toString());
            }

            // Confirm the sign-up by responding to the NEW_PASSWORD_REQUIRED challenge
            // (this calls cognitoSignIn and then adminRespondToAuthChallenge behind the scenes)
            //confirmSignUp(signUp);

            var authResponse = confirmSignUp(signUp);
            if (authResponse == null || authResponse.authenticationResult() == null) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody(new JSONObject().put("error", "User sign-up confirmation failed.").toString());
            }

            // userID
            String userId = Optional.ofNullable(createResponse.user())
                    .map(UserType::username)
                    .orElseThrow(() -> new RuntimeException("Cognito did not return a user ID"));
            String token = authResponse.authenticationResult().idToken();

            Role assignedRole = coachService.isAuthorizedCoach(signUp.email()) ? Role.COACH : Role.CLIENT;

            User newUser = User.builder()
                    .userId(userId)
                    .firstName(signUp.firstName())
                    .lastName(signUp.lastName())
                    .email(signUp.email())
                    .preferableActivity(signUp.preferableActivity())
                    .target(signUp.target())
                    .role(assignedRole)
                    .build();
            userService.saveUser(newUser);

            //confirming
            User savedUser = userService.getUserById(newUser.getUserId());
            if (savedUser == null) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(500)
                        .withBody(new JSONObject()
                                .put("error", "Failed to save user to database.")
                                .toString());
            }
            // Return empty JSON object as per your API spec for 200 response
//            return new APIGatewayProxyResponseEvent()
//                    .withStatusCode(200)
//                    .withBody("{}");

            // If you want to return user info instead, uncomment this:

                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withBody(new JSONObject()
                                .put("userId", createResponse.user().username())
                                .put("token", token)
                                .put("role", assignedRole.name())
                                .toString()
                        );

        } catch (UsernameExistsException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject()
                            .put("error", "A user with this email already exists")
                            .toString());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject()
                            .put("error", e.getMessage())
                            .toString());
        }
    }
}
