package ru.practicum.shareit.user.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    Long id;

    @NotBlank(message = "Name cannot be blank")
    String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    String email;
}