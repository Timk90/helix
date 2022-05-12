package com.helixtesttask.helixdemo.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birthdate")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Column(name = "last_updated_time")
    private LocalDateTime lastUpdatedTime;

    @Column(name = "phone_number")
    private int phoneNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @Max(250)
    @NotNull
    private Integer height;
}
