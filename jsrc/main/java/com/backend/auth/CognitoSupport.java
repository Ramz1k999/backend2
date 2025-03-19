package com.backend.auth;

import com.backend.dto.SignUp;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.inject.Inject;
import java.util.Map;

public class CognitoSupport {
    protected final CognitoIdentityProviderClient cognitoClient;
    protected final String userPoolId;
    protected final String clientId;

    @Inject
    protected CognitoSupport(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
        this.userPoolId = System.getenv("COGNITO_ID");
        this.clientId = System.getenv("CLIENT_ID");

        System.out.println("ClientID: " + System.getenv("CLIENT_ID"));

        if (this.userPoolId == null || this.clientId == null) {
            throw new IllegalStateException("Missing required environment variables: COGNITO_ID or CLIENT_ID");
        }
    }

    protected AdminCreateUserResponse adminCreateUser(SignUp signUp) {
        try {
            AdminCreateUserResponse response = cognitoClient.adminCreateUser(AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(signUp.email())
                    .temporaryPassword(signUp.password())
                    .userAttributes(
                            AttributeType.builder().name("name").value(signUp.firstName()).build(),
                            AttributeType.builder().name("family_name").value(signUp.lastName()).build(),
                            AttributeType.builder().name("email").value(signUp.email()).build(),
                            AttributeType.builder().name("email_verified").value("true").build(),
                            AttributeType.builder().name("custom:target").value(signUp.target().name()).build(),
                            AttributeType.builder().name("custom:activity").value(signUp.preferableActivity().name()).build()
                    )
                    .messageAction("SUPPRESS")
                    .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .forceAliasCreation(false)
                    .build()
            );
            enableUser(signUp.email());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    protected AdminRespondToAuthChallengeResponse confirmSignUp(SignUp signUp) {
        try {
            AdminInitiateAuthResponse authResponse = cognitoSignIn(signUp.email(), signUp.password());

            if (authResponse.challengeNameAsString() == null) {
                return AdminRespondToAuthChallengeResponse.builder()
                        .authenticationResult(authResponse.authenticationResult())
                        .build();
            }

            if (ChallengeNameType.NEW_PASSWORD_REQUIRED.name().equals(authResponse.challengeNameAsString())) {
                Map<String, String> challengeResponses = Map.of(
                        "USERNAME", signUp.email(),
                        "PASSWORD", signUp.password(),
                        "NEW_PASSWORD", signUp.password()
                );

                return cognitoClient.adminRespondToAuthChallenge(AdminRespondToAuthChallengeRequest.builder()
                        .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                        .challengeResponses(challengeResponses)
                        .userPoolId(userPoolId)
                        .clientId(clientId)
                        .session(authResponse.session())
                        .build());
            }
            throw new RuntimeException("Unexpected challenge: " + authResponse.challengeNameAsString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to confirm user sign-up: " + e.getMessage(), e);
        }
    }
    protected void enableUser(String username) {
        try {
            cognitoClient.adminEnableUser(AdminEnableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable user: " + e.getMessage(), e);
        }
    }


    protected AdminInitiateAuthResponse cognitoSignIn(String email, String password) {
        try {
            Map<String, String> authParams = Map.of(
                    "USERNAME", email,
                    "PASSWORD", password
            );

            return cognitoClient.adminInitiateAuth(AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .authParameters(authParams)
                    .userPoolId(userPoolId)
                    .clientId(clientId)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate user: " + e.getMessage(), e);
        }
    }

    protected void changeUserPassword(String username, String oldPassword, String newPassword) {
        try {
            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", username,
                            "PASSWORD", oldPassword
                    ))
                    .userPoolId(userPoolId)
                    .clientId(clientId)
                    .build());

            AuthenticationResultType authResult = authResponse.authenticationResult();
            String accessToken = authResult.accessToken();
            String refreshToken = authResult.refreshToken();

            try {
                changePassword(accessToken, oldPassword, newPassword);
            } catch (NotAuthorizedException e) {
                if (refreshToken != null) {
                    accessToken = refreshAccessToken(refreshToken);
                    changePassword(accessToken, oldPassword, newPassword);
                } else {
                    throw new RuntimeException("Access token expired, and no refresh token available.", e);
                }
            }
        } catch (NotAuthorizedException e) {
            throw new RuntimeException("Invalid credentials or authorization failed.", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while changing the password.", e);
        }
    }


    private void changePassword(String accessToken, String oldPassword, String newPassword) {
        cognitoClient.changePassword(ChangePasswordRequest.builder()
                .accessToken(accessToken)
                .previousPassword(oldPassword)
                .proposedPassword(newPassword)
                .build());
    }

    private String refreshAccessToken(String refreshToken) {
        return cognitoClient.initiateAuth(InitiateAuthRequest.builder()
                        .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                        .authParameters(Map.of("REFRESH_TOKEN", refreshToken))
                        .clientId(clientId)
                        .build())
                .authenticationResult()
                .accessToken();
    }


}