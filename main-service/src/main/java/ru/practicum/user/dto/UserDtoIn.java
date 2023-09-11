package ru.practicum.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDtoIn {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250")
    private String name;
    @NotNull
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Длина имени должна быть от 2 до 250")
    private String email;
}
