package com.app.backend.domain.auth.oauth;

import com.app.backend.domain.user.entity.AuthProvider;
import com.app.backend.global.exception.CustomException;
import com.app.backend.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OAuthClientResolver {

    private final Map<AuthProvider, OAuthClient> clients;

    public OAuthClientResolver(List<OAuthClient> clientList) {
        Map<AuthProvider, OAuthClient> map = new EnumMap<>(AuthProvider.class);
        for (OAuthClient client : clientList) {
            map.put(client.getProvider(), client);
        }
        this.clients = map;
    }

    public OAuthClient resolve(AuthProvider provider) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return client;
    }
}