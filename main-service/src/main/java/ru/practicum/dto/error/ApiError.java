package ru.practicum.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = DateTimeUtils.formatToString(timestamp);
    }
}
