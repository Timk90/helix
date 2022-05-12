package com.helixtesttask.helixdemo.service;

import com.helixtesttask.helixdemo.dto.UserDto;
import com.helixtesttask.helixdemo.mapper.UserMapper;
import com.helixtesttask.helixdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto save(UserDto dto) {
        val savedDao = userRepository.save(UserMapper.toDao(dto));
        return UserMapper.toDto(savedDao);
    }

    public UserDto update(UserDto dto) {
        return UserMapper.toDto(userRepository.save(UserMapper.toDao(dto)));
    }

    public UserDto get(Long id) {
        return UserMapper.toDto(userRepository.getById(id));
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
