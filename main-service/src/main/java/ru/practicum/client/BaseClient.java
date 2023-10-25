package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
        rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, body, MediaType.APPLICATION_JSON);
    }

    protected <T> ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, MediaType.APPLICATION_FORM_URLENCODED);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, T body, MediaType mediaType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(mediaType));
        ResponseEntity<Object> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
