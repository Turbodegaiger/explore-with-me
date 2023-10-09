package ru.practicum.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.EndpointHit;
import ru.practicum.exception.ValidationException;

@Slf4j
public class Validator {
    public static void validateEndpointHit(EndpointHit hit) throws ValidationException {
        if (hit.getIp().isEmpty() || !hit.getIp().contains(".")) {
            log.info("EndpointHit не прошёл валидацию по полю ip");
            throw new ValidationException("EndpointHit не прошёл валидацию по полю ip");
        }
        if (hit.getUri().isEmpty() || !hit.getUri().contains("/")) {
            log.info("EndpointHit не прошёл валидацию по полю uri");
            throw new ValidationException("EndpointHit не прошёл валидацию по полю uri");
        }
        if (hit.getApp().isEmpty()) {
            log.info("EndpointHit не прошёл валидацию по полю app");
            throw new ValidationException("EndpointHit не прошёл валидацию по полю app");
        }
        log.info("EndpointHit прошёл валидацию: {}", hit);
    }
}
