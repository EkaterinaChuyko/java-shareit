package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return post(path, null, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path) {
        return patch(path, null, null);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> put(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, null, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        String fullUrl = "";
        try {

            if (parameters != null && !parameters.isEmpty()) {
                log.debug("Making {} request to {} with parameters: {}", method, path, parameters);
                ResponseEntity<Object> response = rest.exchange(path, method, requestEntity, Object.class, parameters);
                log.debug("Response received: {}", response.getStatusCode());
                return response;
            } else {
                log.debug("Making {} request to {}", method, path);
                ResponseEntity<Object> response = rest.exchange(path, method, requestEntity, Object.class);
                log.debug("Response received: {}", response.getStatusCode());
                return response;
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error {} from server: {} - {}", e.getStatusCode(), path, e.getResponseBodyAsString());

            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(e.getStatusCode()).headers(errorHeaders).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error when calling {}: {}", path, e.getMessage(), e);

            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(errorHeaders).body("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }
}