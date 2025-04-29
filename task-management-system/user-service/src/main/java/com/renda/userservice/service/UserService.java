package com.renda.userservice.service;

import com.renda.userservice.dto.UserRequestDto;
import com.renda.userservice.dto.UserResponseDto;
import com.renda.userservice.entity.User;
import com.renda.userservice.mapper.UserMapper;
import com.renda.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /* ---------- Read Operations ---------- */

    @Transactional(readOnly = true)
    public UserResponseDto findOne(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User ID: " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    /* ---------- Write Operations ---------- */

    public UserResponseDto create(UserRequestDto req) {
        User user = userMapper.toEntity(req);
        user.setCreatedTime(LocalDateTime.now());
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto update(Long id, UserRequestDto req) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User ID: " + id + " not found"));
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        return userMapper.toDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}
