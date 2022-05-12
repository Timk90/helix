package com.helixtesttask.helixdemo.mapper;

import com.helixtesttask.helixdemo.dao.UserDao;
import com.helixtesttask.helixdemo.dto.UserDto;
import lombok.val;

import java.time.LocalDateTime;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(UserDao dao) {
        return UserDto.builder()
                .id(dao.getId())
                .birthDate(dao.getBirthDate())
                .email(dao.getEmail())
                .name(dao.getName())
                .phoneNumber(dao.getPhoneNumber())
                .height(dao.getHeight())
                .lastUpdatedTime(dao.getLastUpdatedTime() == null ? LocalDateTime.now() : dao.getLastUpdatedTime())
                .build();
    }

    public static UserDao toDao(UserDto dto) {
        val dao = UserDao.builder()
                .birthDate(dto.getBirthDate())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .height(dto.getHeight())
                .lastUpdatedTime(dto.getLastUpdatedTime() == null ? LocalDateTime.now() : dto.getLastUpdatedTime())
                .build();
        if (dto.getId() != null) {
            dao.setId(dto.getId());
        }
        return dao;
    }
}
