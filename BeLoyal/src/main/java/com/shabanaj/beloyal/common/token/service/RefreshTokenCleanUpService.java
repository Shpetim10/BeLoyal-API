package com.shabanaj.beloyal.common.token.service;

public interface RefreshTokenCleanUpService {
    void cleanUpRevokedTokens();
}
