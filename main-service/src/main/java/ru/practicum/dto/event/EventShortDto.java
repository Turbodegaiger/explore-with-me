package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    @NotEmpty
    private String annotation;
    @NotEmpty
    private CategoryDto category;
    private Long confirmedRequests;
    @NotEmpty
    private String eventDate;
    private Long id;
    @NotEmpty
    private UserShortDto initiator;
    @NotEmpty
    private Boolean paid;
    @NotEmpty
    private String title;
    private Long views;
}
