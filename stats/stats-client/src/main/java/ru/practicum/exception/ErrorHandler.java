package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongArgument(final MethodArgumentTypeMismatchException exception) {
        log.info("Ошибка: {}. {}", exception.getMessage(), exception.getClass());
        return new ErrorResponse("Некорректно указаны параметры запроса.");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongArgument(final NotFoundException exception) {
        log.info("Ошибка: {}. {}", exception.getMessage(), exception.getClass());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFailedValidation(final ValidationException exception) {
        log.info("Ошибка: {}. {}", exception.getMessage(), exception.getClass());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectRequestParams(final MissingServletRequestParameterException exception) {
        log.info("Ошибка: {}. {}", exception.getMessage(), exception.getClass());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(final Throwable exception) {
        if (exception.getMessage() == null) {
            return new ErrorResponse("Произошла непредвиденная ошибка.");
        }
        log.info(exception.toString());
        return new ErrorResponse(exception.getMessage());
    }
}
