package ru.practicum.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String status;
    private List<ObjectError> errors;
    private String message;
    private String reason;
    private String timestamp;

    public ApiError(List<ObjectError> errors, String message, String reason, HttpStatus status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status.toString();
        setTimestamp(DateTimeUtils.getCurrentTime());
    }

    public ApiError(String message, String reason, HttpStatus status) {
        this.message = message;
        this.reason = reason;
        this.status = status.toString();
        setTimestamp(DateTimeUtils.getCurrentTime());
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = DateTimeUtils.formatToString(timestamp);
    }
}
