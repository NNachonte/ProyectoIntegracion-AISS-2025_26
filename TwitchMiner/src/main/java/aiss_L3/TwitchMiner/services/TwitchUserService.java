package aiss_L3.TwitchMiner.services;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import aiss_L3.TwitchMiner.model.twitch.TwitchUser;
import aiss_L3.TwitchMiner.model.twitch.TwitchUserResponse;

@Service
public class TwitchUserService {

    @Value("${twitch.base-url:https://api.twitch.tv/helix}")
    private String baseUrl;

    @Autowired
    private TwitchClient twitchClient;

    public TwitchUser getUserById(String id) {
        if (!StringUtils.hasText(id)) return null;
        String encoded = UriUtils.encodeQueryParam(id, StandardCharsets.UTF_8);
        String url = baseUrl + "/users?id=" + encoded;
        TwitchUserResponse response = twitchClient.get(url, TwitchUserResponse.class);
        return firstUser(response);
    }

    public TwitchUser getUserByLogin(String login) {
        if (!StringUtils.hasText(login)) return null;
        String encoded = UriUtils.encodeQueryParam(login, StandardCharsets.UTF_8);
        String url = baseUrl + "/users?login=" + encoded;
        TwitchUserResponse response = twitchClient.get(url, TwitchUserResponse.class);
        return firstUser(response);
    }

    public TwitchUser getUserByIdOrLogin(String value) {
        TwitchUser user = getUserById(value);
        if (user != null) return user;
        return getUserByLogin(value);
    }

    private static TwitchUser firstUser(TwitchUserResponse response) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) return null;
        return response.getData().get(0);
    }
}
