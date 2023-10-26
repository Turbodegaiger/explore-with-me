package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Getter
    @Setter
    private String email;
    @Getter
    private Long id;
    @Getter
    @Setter
    private String name;

    @Override
    public String toString() {
        return "UserDto{" +
                "email='" + email + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
