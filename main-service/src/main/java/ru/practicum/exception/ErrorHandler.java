package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.dto.error.ApiError;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException exception) {
        log.info("Не найден запрашиваемый объект. {}", exception.getMessage());
        String reason = "The required object was not found.";
        return new ApiError(exception.getMessage(), reason, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleValidationFailed(final ValidationException exception) {
        log.info("Возникла ошибка валидации, неподходящие данные по формату или содержанию. {}", exception.getMessage());
        String reason = "For the requested operation the conditions are not met.";
        return new ApiError(exception.getMessage(), reason, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationConstraints(final ConstraintViolationException exception) {
        log.info("Возникла ошибка валидации при проверке запроса. {}", exception.getMessage());
        String reason = "The object has not passed validation.";
        return new ApiError(exception.getMessage(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectRequest(final AlreadyExistsException exception) {
        log.info("Прислан некорректный запрос, проверьте аргументы, тело, типы присылаемых данных. {}", exception.getMessage());
        String reason = "Integrity constraint has been violated.";
        return new ApiError(exception.getMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        log.info("Прислан некорректный запрос, проверьте аргументы, тело, типы присылаемых данных. {}", exception.getMessage());
        String reason = "Incorrectly made request.";
        List<ObjectError> errors = exception.getAllErrors();
        return new ApiError(errors.get(0).toString(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            MethodArgumentTypeMismatchException.class,
            InvalidDataAccessApiUsageException.class,
            IncorrectRequestException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatch(final RuntimeException exception) {
        log.info("Прислан некорректный запрос, проверьте аргументы, тело, имена, типы присылаемых данных. {} || {}", exception.getClass(), exception.getMessage());
        String reason = "Incorrectly made request.";
        return new ApiError(exception.getLocalizedMessage(), reason, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDatabaseDataConflict(final RuntimeException exception) {
        String reason = "For the requested operation the conditions are not met.";
        return new ApiError(exception.getLocalizedMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDatabaseDataConflict(final org.springframework.dao.DataIntegrityViolationException exception) {
        log.info("Произошла ошибка, не выполнены условия для загрузки в базу данных (constraints). {}", exception.getClass());
        String reason = "For the requested operation the conditions are not met.";
        return new ApiError(exception.getCause().getCause().getMessage(), reason, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpectedException(final Throwable exception) {
        log.info("Возникла непредвиденная ошибка: {}", exception.toString());
        exception.printStackTrace();
        String reason = "Unexpected server error occurred.";
        return new ApiError(exception.getMessage(), reason, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}