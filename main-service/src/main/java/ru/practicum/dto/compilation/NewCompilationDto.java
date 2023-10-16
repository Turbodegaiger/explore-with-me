package ru.practicum.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NewCompilationDto {
    List<Integer> events;
    Boolean pinned;
    @NotEmpty
    @Size(min = 1, max = 50)
    String title;

    public NewCompilationDto(List<Integer> events, Boolean pinned, String title) {
        if (events != null) {
            this.events = events;
        }
        if (pinned != null) {
            this.pinned = pinned;
        } else {
            this.pinned = false;
        }
        this.title = title;
    }
}
