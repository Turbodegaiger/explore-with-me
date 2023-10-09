package ru.practicum.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    Integer id;
    @NotNull
    String app;
    @NotNull
    String uri;
    @NotNull
    String ip;
    String timestamp;
}
