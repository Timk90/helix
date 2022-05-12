package com.helixtesttask.helixdemo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
public class UserDto {
    private final Long id;
    @NotBlank
    private final String name;
    private final LocalDate birthDate;
    @NotBlank
    private final String email;
    private final Integer phoneNumber;
    @Max(250)
    @NotNull
    private final Integer height;
    private final LocalDateTime lastUpdatedTime;
}
