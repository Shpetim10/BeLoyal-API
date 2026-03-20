package com.shabanaj.beloyal.features.token.service;

public interface RefreshTokenCleanUpService {
    void cleanUpRevokedTokens();
}
