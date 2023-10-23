package ru.practicum.dto.compilation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotEmpty
    @Size(min = 1, max = 50)
    private String title;

    public NewCompilationDto(List<Long> events, Boolean pinned, String title) {
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
