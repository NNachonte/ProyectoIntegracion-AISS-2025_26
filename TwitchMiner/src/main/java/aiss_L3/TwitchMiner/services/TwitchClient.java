package aiss_L3.TwitchMiner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import aiss_L3.TwitchMiner.exception.TwitchApiException;

@Service
public class TwitchClient {

    @Value("${twitch.client-id:}")
    private String clientId;

    @Value("${twitch.token:}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    public <T> T get(String url, Class<T> responseType) {
        validateCredentials();
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // When Twitch API returns 400 Bad Request (invalid ID), return null
            if (e.getStatusCode() != null && e.getStatusCode().value() == 400) {
                return null;
            }
            // For other HTTP client errors, wrap and rethrow as TwitchApiException
            throw new TwitchApiException("Twitch API returned error: " + e.getStatusCode() + " - " + e.getMessage(), e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private void validateCredentials() {
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(token)) {
            throw new TwitchApiException("Missing Twitch credentials. Set TWITCH_CLIENT_ID and TWITCH_TOKEN.");
        }
    }
}
